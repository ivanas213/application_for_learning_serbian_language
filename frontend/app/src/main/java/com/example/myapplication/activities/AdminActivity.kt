package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.adapters.TopicAdapter
import com.example.myapplication.adapters.UserAdapter
import com.example.myapplication.models.Topic
import retrofit2.Callback
import com.example.myapplication.models.User
import retrofit2.Call
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    val retrofitInterface= RetrofitInstance.retrofitInterface
    private lateinit var  sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPreferences=getSharedPreferences("user_session", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_admin)
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView?.layoutManager = LinearLayoutManager(this)
        val token = sharedPreferences.getString("AUTH_TOKEN", null)
        val logout=findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            logout()
        }

        retrofitInterface.getUsers("Bearer $token").enqueue(
            object : Callback<List<User>> {
                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    val users=response.body()
                    if(users!=null)
                        recyclerView.adapter=UserAdapter(users,this@AdminActivity)
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                }

            }
        )

    }
    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.remove("AUTH_TOKEN")
        editor.apply()

        val intent = Intent(this@AdminActivity,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}