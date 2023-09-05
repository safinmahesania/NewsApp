package com.project.newsapp.contract

data class Response(
    var status:Boolean = false,
    var responseCode:Int = -1,
    var message:String = "",
    var userId:String = "",
    var newsId:String = "",
    var userName:String = "",
    var allNews:MutableList<News> = mutableListOf(),
    var myNews:MutableList<News> = mutableListOf()
)
