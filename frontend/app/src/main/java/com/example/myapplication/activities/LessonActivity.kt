package com.example.myapplication.activities

import ContentBlock
import Lesson
import QuestionBlock
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.adapters.LessonAdapter
import com.example.myapplication.viewHolders.FooterViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LessonActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var adapter:LessonAdapter?=null
    private lateinit var recyclerView:RecyclerView
    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private var lesson:Lesson?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        sharedPreferences=getSharedPreferences("user_session", MODE_PRIVATE)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val id = sharedPreferences.getString("current_lesson_id", null)
        val token = sharedPreferences.getString("AUTH_TOKEN", null)

        if (id != null) {
            if (token != null) {
                retrofitInterface.getLessonById(token,id).enqueue(object:Callback<Lesson>{
                    override fun onResponse(call: Call<Lesson>, response: Response<Lesson>) {
                        lesson= response.body()!!
                        adapter= LessonAdapter(lesson!!.contentBlocks,recyclerView, this@LessonActivity, lesson!!.title)
                        adapter!!.setNewLesson(lesson!!.contentBlocks)
                        recyclerView.adapter = adapter
                    }

                    override fun onFailure(call: Call<Lesson>, t: Throwable) {
                    }

                })
            }


        }

    }
    fun setPadding(){
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            val recyclerHeight = recyclerView.height
            val screenHeight = recyclerView.rootView.height

            if (recyclerHeight < screenHeight) {
                recyclerView.setPadding(0, 0, 0, screenHeight - recyclerHeight)
            } else {
                recyclerView.setPadding(0, 0, 0, 0)
            }
        }
    }



}
