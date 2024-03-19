package com.dicoding.githubapp.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.githubapp.R
import com.dicoding.githubapp.data.response.DetailUserResponse
import com.dicoding.githubapp.databinding.ActivityDetailUserBinding
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    val detailViewModel by viewModels<DetailViewModel>()

    companion object{
        const val EXTRA_USERNAME = "extra_username"

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

        val username = intent.getStringExtra(EXTRA_USERNAME)

        if (username != null) {
            detailViewModel.getUser(username)
        }
        detailViewModel.userObject.observe(this){detail ->
            setUserDetail(detail)
        }

        detailViewModel.isLoading.observe(this){
            showLoading(it)
        }

        val sectionPagerAdapter = SectionPagerAdapter(this, username ?: "")
        val viewPager = binding.viewPagerDetail
        viewPager.adapter = sectionPagerAdapter
        val tab = binding.tabDetail
        TabLayoutMediator(tab, viewPager) { tab, position->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()


    }

    private fun setUserDetail(detail : DetailUserResponse) {
        binding.apply {
            tvUsernameDetail.text = detail.login
            tvNamaDetail.text = detail.name
            tvFollowerDetail.text = "${detail.followers} Followers"
            tvFollowingDetail.text = "${detail.following} Following"
            Glide.with(this@DetailUserActivity)
                .load(detail.avatarUrl)
                .into(imageDetail)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}