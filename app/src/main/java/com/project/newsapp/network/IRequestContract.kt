package com.project.newsapp.network

import com.project.newsapp.contract.Request
import com.project.newsapp.contract.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IRequestContract {
    @POST("service.php")
    fun makeApiCall(@Body request: Request): Call<Response>
}