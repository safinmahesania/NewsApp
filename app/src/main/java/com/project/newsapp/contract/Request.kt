package com.project.newsapp.contract

import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("action") var action:String = "",
    @SerializedName("userId") var userId:String = "",
    @SerializedName("userName") var userName:String = "",
    @SerializedName("newsId") var newsId:String = "",
    @SerializedName("headline") var headline:String = "",
    @SerializedName("description") var description:String = ""
)
