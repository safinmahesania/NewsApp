package com.project.newsapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.newsapp.contract.News
import com.project.newsapp.contract.Request
import com.project.newsapp.contract.Response
import com.project.newsapp.network.IRequestContract
import com.project.newsapp.network.NetworkClient
import com.project.newsapp.utils.Constant
import com.project.newsapp.utils.DataProvider
import com.project.newsapp.utils.showToast
import kotlinx.android.synthetic.main.activity_add_or_update_news.*
import retrofit2.Call
import retrofit2.Callback

class AddOrUpdateNewsActivity : AppCompatActivity(), Callback<Response> {

    lateinit var userId:String
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private var reason:Int = 0
    private lateinit var editedNews: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_update_news)

        title = "Add/Update"

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(true)

        sharedPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)

        userId = sharedPreferences.getString(Constant.KEY_USER_ID,"").toString()
        reason = intent.getIntExtra(Constant.KEY_REASON, 0)

        renderUIForEdit()

        btnPost.setOnClickListener {
            val headline = edHeadline.text.toString().trim()
            val description = edDescription.text.toString().trim()

            if(headline.isNotEmpty() && description.isNotEmpty()){
                var request = Request()
                if(reason == 2){
                    request = Request(
                        action = Constant.UPDATE_NEWS,
                        userId = userId,
                        newsId = editedNews.newsId,
                        headline = headline,
                        description = description
                    )
                }else{
                    request = Request(
                        action = Constant.ADD_NEWS,
                        userId = userId,
                        headline = headline,
                        description = description
                    )
                }
                progressDialog.show()
                val callResponse = requestContract.makeApiCall(request)
                callResponse.enqueue(this)
            }else{
                showToast("Headline or Description can't be empty")
            }
        }
    }

    private fun renderUIForEdit(){
        if(reason == 2){
            editedNews = DataProvider.news
            edHeadline.setText(editedNews.headline)
            edDescription.setText(editedNews.description)
            btnPost.text = resources.getString(R.string.update)
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        showToast("Server is not responding. Please try again later.")
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        if(response.body()!=null){
            val serverResponse = response.body()
            if(serverResponse!!.status){
                showToast(serverResponse.message)
                Intent(this, HomeActivity::class.java).apply {
                    println("Before finishing AddOrUpdateNewsActivity userId is: "+userId)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(this)
                }
            }else{
                showToast(serverResponse.message)
            }
        }
        else{
            showToast("Server is not responding. Please try again later.")
        }
    }
}