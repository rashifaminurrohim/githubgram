package com.dicoding.githubapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubapp.data.response.GithubUserResponse
import com.dicoding.githubapp.data.response.ItemsItem
import com.dicoding.githubapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class MainViewModel : ViewModel() {

    private val _listUser = MutableLiveData<List<ItemsItem>>()
    val listUser: LiveData<List<ItemsItem>> = _listUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val TAG = "HomeViewModel"
        private const val QUERY = "rashif"
    }

    init {
        lizzUsers()
    }

    private fun lizzUsers() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserGithub(QUERY)
        client.enqueue(object : Callback<GithubUserResponse> {
            override fun onResponse(call: Call<GithubUserResponse>, response: Response<GithubUserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listUser.value = response.body()?.items
                }
            }
            override fun onFailure(call: Call<GithubUserResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    internal fun searchUser(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserGithub(query)
        client.enqueue(object : Callback<GithubUserResponse> {
            override fun onResponse(call: Call<GithubUserResponse>, response: Response<GithubUserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listUser.value = response.body()?.items
                }
            }
            override fun onFailure(call: Call<GithubUserResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })

    }

}