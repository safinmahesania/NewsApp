package com.project.newsapp.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.newsapp.AddOrUpdateNewsActivity
import com.project.newsapp.R
import com.project.newsapp.contract.News
import com.project.newsapp.contract.Request
import com.project.newsapp.contract.Response
import com.project.newsapp.network.IRequestContract
import com.project.newsapp.network.NetworkClient
import com.project.newsapp.utils.Constant
import com.project.newsapp.utils.DataProvider
import com.project.newsapp.utils.showToast
import kotlinx.android.synthetic.main.it_my_news.view.*
import kotlinx.android.synthetic.main.it_my_news.view.dateTime
import kotlinx.android.synthetic.main.it_my_news.view.description
import kotlinx.android.synthetic.main.it_my_news.view.headline
import retrofit2.Call
import retrofit2.Callback
import kotlin.properties.Delegates


class MyNewsAdapter(var activity: Activity, var context: Context, var dataSource:MutableList<News>):RecyclerView.Adapter<MyNewsAdapter.MyNewsViewHolder>(),Callback<Response> {

    private var progressDialog:ProgressDialog = ProgressDialog(context)

    private val retrofitClient = NetworkClient.getNetworkClient()
    private val requestContract = retrofitClient.create(IRequestContract::class.java)
    private lateinit var deletedNews:News
    private var deletedPosition:Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.it_my_news,parent,false)
        return MyNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyNewsViewHolder, position: Int) {
        val news = dataSource[position]
        holder.headline.text = news.headline
        holder.description.text = news.description
        holder.dateTime.text = news.dateTime

        holder.btnEdit.setOnClickListener{
            Intent(context, AddOrUpdateNewsActivity::class.java).apply {
                DataProvider.news = news
                putExtra(Constant.KEY_REASON,2) //2 Means UPDATE
                activity.startActivity(this)
            }
        }

        holder.btnDelete.setOnClickListener{
            AlertDialog.Builder(context)
                .setTitle("NewsApp Alert")
                .setMessage("You want to delete this news?")
                .setPositiveButton("Yes"){ dialog, which ->
                    progressDialog.setMessage("Please Wait...")
                    progressDialog.setCancelable(false)
                    deletedNews = news
                    deletedPosition = position
                    val request = Request(
                        action = Constant.DELETE_NEWS,
                        userId = DataProvider.userId,
                        newsId = news.newsId
                    )
                    progressDialog.show()
                    val callResponse = requestContract.makeApiCall(request)
                    callResponse.enqueue(this)
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    class MyNewsViewHolder(view: View): RecyclerView.ViewHolder(view){
        var headline = view.headline
        var dateTime = view.dateTime
        var description = view.description
        var btnEdit = view.edit
        var btnDelete = view.delete
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        context.showToast("Server is not responding. Please try again later.")
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if(progressDialog.isShowing)
            progressDialog.dismiss()

        if(response.body()!=null){
            val serverResponse = response.body()
            if(serverResponse!!.status){
                dataSource.remove(deletedNews)
                notifyItemRemoved(deletedPosition)
                notifyItemRangeChanged(deletedPosition,dataSource.size)
                context.showToast(serverResponse.message)
            }else{
                context.showToast(serverResponse.message)
            }
        }
        else{
            context.showToast("Server is not responding. Please try again later.")
        }
    }
}
