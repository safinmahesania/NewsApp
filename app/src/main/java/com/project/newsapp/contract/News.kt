package com.project.newsapp.contract

import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("newsId") var newsId:String = "",
    @SerializedName("newsBy") var newsBy:String = "",
    @SerializedName("headline") var headline:String = "",
    @SerializedName("description") var description:String = "",
    @SerializedName("dateTime") var dateTime:String = ""
)
