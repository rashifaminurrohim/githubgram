package com.dicoding.githubapp.ui

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.githubapp.R
import com.dicoding.githubapp.data.local.entity.FavEntity
import com.dicoding.githubapp.data.remote.response.DetailUserResponse
import com.dicoding.githubapp.databinding.ActivityDetailUserBinding
import com.dicoding.githubapp.helper.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator


class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private val detailViewModel by viewModels<DetailViewModel>()
    private lateinit var favViewModel: FavViewModel


    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val REQ_DETAIL_USER = 1

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers_detail,
            R.string.following_detail
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.bg_blue
                )
            )
        )
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "User Detail"

        favViewModel = obtainViewModel(this@DetailUserActivity)

        val username = intent.getStringExtra(EXTRA_USERNAME)

        if (username != null) {
            detailViewModel.getUser(username)
        }
        detailViewModel.userObject.observe(this) { detail ->
            setUserDetail(detail)
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        val sectionPagerAdapter = SectionPagerAdapter(this, username ?: "")
        val viewPager = binding.viewPagerDetail
        viewPager.adapter = sectionPagerAdapter
        val tab = binding.tabDetail
        TabLayoutMediator(tab, viewPager) { tabs, position ->
            tabs.text = resources.getString(TAB_TITLES[position])
        }.attach()


        favViewModel.checkFav(username ?: "").observe(this) { count ->
            if (count != null) {
                val isFav = count > 0
                binding.favFab.setImageResource(if (isFav) R.drawable.ic_favourited else R.drawable.ic_favourite)
                binding.favFab.setOnClickListener {
                    val favEntity = createFavEntity(detailViewModel.userObject.value!!)
                    if (isFav) {
                        deleteFavourite(favEntity)
                    } else {
                        addFavourite(favEntity)
                    }
                }
            }
        }

    }

    private fun setUserDetail(detail: DetailUserResponse) {
        binding.apply {
            tvUsernameDetail.text = detail.login
            tvNamaDetail.text = detail.name
            tvFollowerDetail.text = StringBuilder(detail.followers.toString()).append(" Followers")
            tvFollowingDetail.text = StringBuilder(detail.following.toString()).append(" Following")
            Glide.with(this@DetailUserActivity)
                .load(detail.avatarUrl)
                .into(imageDetail)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun obtainViewModel(activity: AppCompatActivity): FavViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavViewModel::class.java]
    }

    private fun createFavEntity(detail: DetailUserResponse): FavEntity {
        return FavEntity(
            username = detail.login,
            avatarUrl = detail.avatarUrl
        )
    }

    private fun addFavourite(favEntity: FavEntity) {
        favViewModel.insert(favEntity)
        binding.favFab.setImageResource(R.drawable.ic_favourited)
    }

    private fun deleteFavourite(favEntity: FavEntity) {
        favViewModel.deleteFav(favEntity)
        setResult(Activity.RESULT_OK)
        binding.favFab.setImageResource(R.drawable.ic_favourite)
    }


}