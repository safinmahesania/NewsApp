package com.project.newsapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.newsapp.contract.Request
import com.project.newsapp.contract.Response
import com.project.newsapp.network.IRequestContract
import com.project.newsapp.network.NetworkClient
import com.project.newsapp.utils.Constant
import com.project.newsapp.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback

class MainActivity : AppCompatActivity(), Callback<Response> {

    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var progressDialog:ProgressDialog
    private lateinit var sharedPreferences:SharedPreferences
    lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "NewsApp Register"

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(true)

        sharedPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)

        checkIfUserAlreadyRegistered()

        btnLogin.setOnClickListener{
            Intent(this,LoginActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }

        btnRegister.setOnClickListener {
            userName = edUserName.text.toString().trim().uppercase()
            if(userName.isNullOrEmpty()){
                showToast("Please Enter your Name")
            }else{
                progressDialog.show()
                val request = Request(
                    action = Constant.REGISTER_USER,
                    userName = userName
                )
                val callResponse = requestContract.makeApiCall(request)
                callResponse.enqueue(this)
            }
        }

        btnExit.setOnClickListener {
            finish()
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        showToast("Server is not responding. Please try again later.")
        edUserName.setText("")
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        if(response.body()!=null){
            val serverResponse = response.body()
            if(serverResponse!!.status){
                saveUserToPref(serverResponse.userId,userName)
                Intent(this, HomeActivity::class.java).apply {
                    putExtra(Constant.KEY_USER_ID, serverResponse.userId)
                    putExtra(Constant.KEY_USER_NAME, userName)
                    startActivity(this)
                    finish()
                }
            }else{
                showToast(serverResponse.message)
                edUserName.setText("")
            }
        }
        else{
            showToast("Server is not responding. Please try again later.")
            edUserName.setText("")
        }
    }

    private fun saveUserToPref(userId:String, userName:String){
        val editor = sharedPreferences.edit()
        editor.putString(Constant.KEY_USER_ID,userId)
        editor.putString(Constant.KEY_USER_NAME,userName)
        editor.commit()
    }

    private fun checkIfUserAlreadyRegistered(){
        val userId = sharedPreferences.getString(Constant.KEY_USER_ID,"invalid user id")
        val userName = sharedPreferences.getString(Constant.KEY_USER_NAME,"invalid user name")

        if(!userId.contentEquals("invalid user id")
            && !userName.contentEquals("invalid user name")){
            Intent(this, HomeActivity::class.java).apply {
                putExtra(Constant.KEY_USER_ID, userId)
                putExtra(Constant.KEY_USER_NAME, userName)
                startActivity(this)
                finish()
            }
        }
    }
}