package com.example.myapplication.adapters

import Lesson
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.activities.LessonActivity
import com.example.myapplication.activities.LoginActivity
import com.example.myapplication.models.LessonTitle
import com.example.myapplication.models.Topic
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopicAdapter(private val context:Context,private val topics: List<Topic>) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {
    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private lateinit var sharedPreferences: SharedPreferences

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicName: TextView = itemView.findViewById(R.id.topicName)
        val lessonContainer: RecyclerView = itemView.findViewById(R.id.lessonContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        sharedPreferences=context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val topic = topics[position]
        holder.topicName.text = topic.name

        val backgroundColor = when (topic.type) {
            "граматика" -> ContextCompat.getColor(holder.itemView.context, R.color.grammar)
            "правопис" -> ContextCompat.getColor(holder.itemView.context, R.color.spelling)
            else -> ContextCompat.getColor(holder.itemView.context, R.color.literature)
        }
        holder.topicName.setBackgroundColor(backgroundColor)

        val token = sharedPreferences.getString("AUTH_TOKEN", null)

        val call = retrofitInterface.getLessonTitlesByTopic("Bearer $token", topic._id)
        call.enqueue(object : Callback<List<LessonTitle>> {
            override fun onResponse(call: Call<List<LessonTitle>>, response: Response<List<LessonTitle>>) {
                val code=response.code()
                if(code==200){
                    val lessons = response.body()
                    if (lessons != null) {
                        holder.lessonContainer.layoutManager = LinearLayoutManager(context)
                        holder.lessonContainer.adapter = LessonTopicAdapter(lessons, topic.type, context,topics[position]._id)
                    }
                }
                else if(code==401){
                    logout()
                }

            }



            override fun onFailure(call: Call<List<LessonTitle>>, t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()            }
        })
    }


    override fun getItemCount(): Int = topics.size
    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.remove("AUTH_TOKEN")
        editor.apply()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)

        if (context is Activity) {
            context.finish()
        }
    }

}
