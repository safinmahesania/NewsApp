package com.project.newsapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.newsapp.contract.Request
import com.project.newsapp.contract.Response
import com.project.newsapp.network.IRequestContract
import com.project.newsapp.network.NetworkClient
import com.project.newsapp.utils.Constant
import com.project.newsapp.utils.DataProvider
import com.project.newsapp.utils.showToast
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.btnExit
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback

class HomeActivity : AppCompatActivity(), Callback<Response>, View.OnClickListener {

    lateinit var userId:String
    lateinit var userName:String
    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(true)

        sharedPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)

        title = "NewsApp Dashboard"
        userId = sharedPreferences.getString(Constant.KEY_USER_ID,"").toString()
        userName = sharedPreferences.getString(Constant.KEY_USER_NAME,"").toString()

        DataProvider.userId = userId
        DataProvider.userName = userName

        txtUserName.text = "Welcome $userName"

        btnAllNews.setOnClickListener (this)

        btnMyNews.setOnClickListener (this)

        btnSignOut.setOnClickListener (this)

        btnExit.setOnClickListener (this)

    }

    override fun onStart() {
        super.onStart()
        progressDialog.show()
        val request = Request(
            action = Constant.GET_NEWS,
            userId = userId
        )
        val callResponse = requestContract.makeApiCall(request)
        callResponse.enqueue(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAllNews -> {
                if(DataProvider.response.allNews.size>0){
                    Intent(this,ViewAllNewsActivity::class.java).apply {
                        startActivity(this)
                    }
                }else{
                    showToast("Blogs are not available")
                }
            }
            R.id.btnMyNews -> {
                Intent(this,ViewMyNewsActivity::class.java).apply {
                    startActivity(this)
                }
            }
            R.id.btnSignOut -> {
                signOut()
            }
            R.id.btnExit ->{
                finish()
            }
        }
    }
    private fun signOut(){
        val editor = sharedPreferences.edit()
        editor.clear().commit()

        Intent(this,MainActivity::class.java).apply{
            startActivity(this)
            finish()
        }
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        if(response.body()!=null){
            val serverResponse = response.body()
            if(serverResponse!!.status){
                DataProvider.response = serverResponse
            }else{
                showToast(serverResponse.message)
            }
        }
        else{
            showToast("Server is not responding. Please try again later.")
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        showToast("Server is not responding. Please try again later.")
    }
}