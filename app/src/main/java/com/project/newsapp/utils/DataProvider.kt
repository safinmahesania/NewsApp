package com.project.newsapp.utils

import com.project.newsapp.contract.News
import com.project.newsapp.contract.Response

object DataProvider {
    var response:Response = Response()
    var news:News = News()
    lateinit var userId:String
    lateinit var userName:String
}