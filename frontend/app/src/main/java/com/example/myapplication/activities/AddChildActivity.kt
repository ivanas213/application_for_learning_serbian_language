package com.example.myapplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.adapters.AvatarAdapter
import com.example.myapplication.models.Avatar
import com.example.myapplication.models.Child
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddChildActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private lateinit var avatarsRecyclerView: RecyclerView
    private var selectedAvatar: Avatar? = null
    private lateinit var nickname:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_child)
        avatarsRecyclerView = findViewById(R.id.rvAvatars)
        avatarsRecyclerView.layoutManager = LinearLayoutManager(this)
        sharedPreferences=getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nicknameSection = findViewById<LinearLayout>(R.id.nicknameSection)
        val avatarSection = findViewById<LinearLayout>(R.id.avatarSection)
        findViewById<Button>(R.id.btnNextToBirthdate).setOnClickListener {
            val etNickname=findViewById<EditText>(R.id.etAddChildNickname)
            if(etNickname.text.length==0){
                val etError=findViewById<TextView>(R.id.twAddChildError)
                etError.visibility=View.VISIBLE
                etError.text="Изабери надимак!"
            }
            if(isNicknameTaken(etNickname.text.toString())){
                val etError=findViewById<TextView>(R.id.twAddChildError)
                etError.visibility=View.VISIBLE
                etError.text="Изабери неки други надимак јер је овај већ у употреби!"
            }
            else {
                nickname=etNickname.text.toString()
                nicknameSection.visibility = View.GONE
                avatarSection.visibility = View.VISIBLE
                fetchAvatars()

            }
        }



        findViewById<Button>(R.id.btnFinish).setOnClickListener {
            val token = sharedPreferences.getString("AUTH_TOKEN", null)
            val map = HashMap<String, String>()
            map["name"]=nickname
            if(selectedAvatar!=null)
            map["image"]= selectedAvatar!!._id
            val call = retrofitInterface.addChild("Bearer $token",map)
            call.enqueue(object:Callback<Child>{
                override fun onResponse(call: Call<Child>, response: Response<Child>) {
                    if (response.isSuccessful){
                        Intent(this@AddChildActivity,SelectChildActivity::class.java).also{
                            startActivity(it)
                            finish()
                        }
                    }

                }

                override fun onFailure(call: Call<Child>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    private fun isNicknameTaken(nickname: String): Boolean {
        val json = sharedPreferences.getString("CHILDREN", null)
        if (json != null) {
            val type = object : TypeToken<List<Child>>() {}.type
            val children: List<Child> = Gson().fromJson(json, type)
            return children.any { it.name == nickname }
        }
        return false
    }
    private fun fetchAvatars() {

        val call: Call<List<Avatar>> = retrofitInterface.getAvatars()

        call.enqueue(object : Callback<List<Avatar>> {
            override fun onResponse(call: Call<List<Avatar>>, response: Response<List<Avatar>>) {
                if (response.isSuccessful) {
                    val avatars = response.body()
                    if (avatars != null) {
                        updateAvatarsRecyclerView(avatars)
                    }
                } else {

                    Toast.makeText(this@AddChildActivity, "Neuspešno preuzimanje avatara", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Avatar>>, t: Throwable) {
                Toast.makeText(this@AddChildActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateAvatarsRecyclerView(avatars: List<Avatar>) {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        val avatarWidth = resources.getDimensionPixelSize(R.dimen.avatar_width)

        val spanCount = (screenWidth / avatarWidth).coerceAtLeast(1)

        avatarsRecyclerView.layoutManager = GridLayoutManager(this, spanCount)

        val adapter = AvatarAdapter(avatars) { avatar ->
            selectedAvatar = avatar
        }
        avatarsRecyclerView.adapter = adapter
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("visibleSection", getCurrentVisibleSection())
    }

    private fun getCurrentVisibleSection(): Int {
        return when {
            findViewById<LinearLayout>(R.id.nicknameSection).visibility == View.VISIBLE -> 0
            findViewById<LinearLayout>(R.id.birthdateSection).visibility == View.VISIBLE -> 1
            findViewById<LinearLayout>(R.id.avatarSection).visibility == View.VISIBLE -> 2
            else -> 0
        }
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        when (savedInstanceState.getInt("visibleSection", 0)) {
            0 -> {
                findViewById<LinearLayout>(R.id.nicknameSection).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.birthdateSection).visibility = View.GONE
                findViewById<LinearLayout>(R.id.avatarSection).visibility = View.GONE
            }
            1 -> {
                findViewById<LinearLayout>(R.id.nicknameSection).visibility = View.GONE
                findViewById<LinearLayout>(R.id.birthdateSection).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.avatarSection).visibility = View.GONE
            }
            2 -> {
                findViewById<LinearLayout>(R.id.nicknameSection).visibility = View.GONE
                findViewById<LinearLayout>(R.id.birthdateSection).visibility = View.GONE
                findViewById<LinearLayout>(R.id.avatarSection).visibility = View.VISIBLE
            }
        }
    }

}