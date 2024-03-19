package com.dicoding.githubapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubapp.data.response.ItemsItem
import com.dicoding.githubapp.databinding.FragmentFollowBinding


class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!
    val followViewModel by viewModels<FollowViewModel>()
    private val adapter = UserAdapter()


    companion object{
        const val ARG_POSITION = "arg_position"
        const val ARG_USERNAME = "arg_username"

    }

    private var position: Int = 0
    private var username: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFollow.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding.rvFollow.addItemDecoration(itemDecoration)


        followViewModel.isLoading.observe(requireActivity()){
            showLoading(it)
        }

        followViewModel.follow.observe(requireActivity()){ listFollow ->
            setFollowData(listFollow)
        }

        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }
        showLoading(true)
        if (position == 1){
            username?.let { getFollower(it) }

        } else {
            username?.let { getFollowing(it) }

        }

    }

    fun getFollower(username: String) {
        followViewModel.getFollow(username, "followers")
    }

    fun getFollowing(username: String) {
        followViewModel.getFollow(username, "following")
    }

    private fun setFollowData (listFollow : List<ItemsItem>){
        adapter.setOnItemClickCallback(object :UserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ItemsItem) {
                Intent(requireActivity(), DetailUserActivity::class.java).apply {
                    this.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    startActivity(this)
                }
            }

        })
        adapter.submitList(listFollow)
        binding.rvFollow.adapter = adapter
    }



    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}