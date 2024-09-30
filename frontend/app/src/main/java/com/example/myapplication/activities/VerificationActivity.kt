package com.example.myapplication.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.ErrorHandlingCallback
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.models.MessageResult
import com.example.myapplication.models.RegisterResult
import retrofit2.Call
import retrofit2.Response

class VerificationActivity : AppCompatActivity() {
    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = intent.getStringExtra("email")

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val verText=findViewById<TextView>(R.id.twVerificationText)
        verText.text="Послали смо вам имејл са верификационим линком на $email. Молимо вас да кликнете на тај линк какобисте верификовали своју адресу  и завршили регистрацију."
        val btnVerification=findViewById<Button>(R.id.btnVerficationAgain)
        val twBack=findViewById<TextView>(R.id.twVerificationBack)
        val arrow=findViewById<ImageView>(R.id.srcVerificationArrow)
        btnVerification.setOnClickListener{
            val map=HashMap<String,String>()

            val password = intent.getStringExtra("password")

            map["email"]=email?:""
            map["password"]=password?:""
            val call: Call<RegisterResult> = retrofitInterface.executeRegister(map)

            call.enqueue(object : ErrorHandlingCallback<RegisterResult>() {
                override fun onSuccess(call: Call<RegisterResult>, response: Response<RegisterResult>) {
                    Toast.makeText(this@VerificationActivity, "Поново послато", Toast.LENGTH_SHORT).show()                }

                override fun onError(call: Call<RegisterResult>, errorResponse: MessageResult) {
                    val twError = findViewById<TextView>(R.id.twRegisterError)
                    twError.text = errorResponse.getMessage()
                }
            })
        }
        twBack.setOnClickListener{
            finish()
        }
        arrow.setOnClickListener{
            finish()
        }
    }

}