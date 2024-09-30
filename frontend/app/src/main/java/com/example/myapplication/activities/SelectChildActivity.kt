package com.example.myapplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.adapters.ChildAdapter
import com.example.myapplication.models.Child
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectChildActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val retrofitInterface = RetrofitInstance.retrofitInterface
    private lateinit var children: List<Child>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_child)
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        enableEdgeToEdge()
        val token = sharedPreferences.getString("AUTH_TOKEN", null)
        if (token == null){
            val uri: Uri? = intent.data
            uri?.let {
                val jwtToken = it.getQueryParameter("token")

                jwtToken?.let { token ->
                    val editor = sharedPreferences.edit()
                    editor.putString("AUTH_TOKEN", token)
                    editor.apply()
                }
            }
            sharedPreferences.edit().putString("type","user")
            sharedPreferences.edit().apply()
        }
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val btnAddProfile=findViewById<Button>(R.id.btnAddProfile)
        btnAddProfile.setOnClickListener{
            Intent(this@SelectChildActivity,AddChildActivity::class.java).also{
                startActivity(it)
            }
        }
        loadChildren()


    }

    private fun loadChildren() {
        val token = sharedPreferences.getString("AUTH_TOKEN", null)
        if (token != null) {
            val call = retrofitInterface.getChildren("Bearer $token")
            call.enqueue(object : Callback<List<Child>> {
                override fun onResponse(call: Call<List<Child>>, response: Response<List<Child>>) {
                    if (response.isSuccessful) {
                        children = response.body() ?: listOf()
                        saveChildrenToSharedPreferences(children)
                        val btnAddProfile=findViewById<Button>(R.id.btnAddProfile)
                        if (children.size<4)
                        btnAddProfile.setVisibility(View.VISIBLE)
                        else{
                            btnAddProfile.setVisibility(View.GONE)

                        }
                        setupRecyclerView(children)
                    }

                }

                override fun onFailure(call: Call<List<Child>>, t: Throwable) {
                }
            })
        } else {
            val intent = Intent(this@SelectChildActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView(children: List<Child>) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewProfiles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ChildAdapter(this,children)
        recyclerView.visibility = if (children.isNotEmpty()) View.VISIBLE else View.GONE
    }
    private fun saveChildrenToSharedPreferences(children: List<Child>) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(children)
        editor.putString("CHILDREN", json)
        editor.apply()
    }
}

