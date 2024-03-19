package com.dicoding.githubapp.data.retrofit

import com.dicoding.githubapp.data.response.DetailUserResponse
import com.dicoding.githubapp.data.response.GithubUserResponse
import com.dicoding.githubapp.data.response.ItemsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/users")
    fun getUserGithub ( @Query("q") query: String ) : Call<GithubUserResponse>

    @GET("users/{username}")
    fun getDetailUser(@Path("username") username: String): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollowers(@Path("username") username: String): Call<List<ItemsItem>>

    @GET("users/{username}/following")
    fun getFollowing(@Path("username") username: String): Call<List<ItemsItem>>
}