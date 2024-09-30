package com.example.myapplication.activities


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.ErrorHandlingCallback
import com.example.myapplication.models.LoginResult
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.models.MessageResult
import retrofit2.Call
import retrofit2.Callback;
import retrofit2.Response
import android.graphics.Color;


class LoginActivity : AppCompatActivity() {

    private var retrofitInterface=RetrofitInstance.retrofitInterface
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clearFields()
        val authToken = sharedPreferences.getString("AUTH_TOKEN", null)
        val type=sharedPreferences.getString("type",null)
        if (authToken != null) {
            if(type=="user")
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            else{
                Intent(this, AdminActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
        val regTW=findViewById<TextView>(R.id.twRegister)
        val loginButton=findViewById<Button>(R.id.btnlogin)
        val forgottenPassword=findViewById<TextView>(R.id.twLoginForgottenPassword)
        regTW.setOnClickListener{
            Intent(this, RegisterActivity::class.java).also{
                startActivity(it)
            }
        }
        val email=findViewById<EditText>(R.id.etLoginEmail)

        forgottenPassword.setOnClickListener(){
            if(email.text.toString().length==0){
               findViewById<TextView>(R.id.twLoginError).text="Морате унети имејл адресу да бисте затражили промену лозинке."
            }
            else{
                findViewById<TextView>(R.id.twLoginError).text=""
                val map=HashMap<String,String>()
                map["email"]=email.text.toString()
                val call:Call<MessageResult> =retrofitInterface.forgottenPassword(map)
                call.enqueue(object : ErrorHandlingCallback<MessageResult>() {
                    override fun onSuccess(
                        call: Call<MessageResult>,
                        response: Response<MessageResult>
                    ) {
                        val message= response.body()?.getMessage()
                        val twError = findViewById<TextView>(R.id.twLoginError)
                        if(response.code()==200){
                            Toast.makeText(this@LoginActivity,message,Toast.LENGTH_SHORT).show()
                        }
                        else {
                            twError.setTextColor(Color.parseColor("#FF0000"));
                            twError.text = message

                        }

                    }

                    override fun onError(call: Call<MessageResult>, errorResponse: MessageResult) {

                    }

                })
            }

        }
        loginButton.setOnClickListener{
            handleLogin()
            val scaleX = ObjectAnimator.ofFloat(loginButton, "scaleX", 1f, 1.1f, 1f)
            val scaleY = ObjectAnimator.ofFloat(loginButton, "scaleY", 1f, 1.1f, 1f)

            scaleX.duration = 200
            scaleY.duration = 200

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleX, scaleY)
            animatorSet.start()
        }
        val etLoginPassword = findViewById<EditText>(R.id.etLoginPassword)
        val srcEye = findViewById<ImageView>(R.id.srcEye)

        var isPasswordVisible = true

        srcEye.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                srcEye.setImageResource(R.drawable.eye)
                etLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                srcEye.setImageResource(R.drawable.eyex)
            }
            etLoginPassword.setSelection(etLoginPassword.text.length)
        }


    }

    private fun handleLogin() {

        var isValid=true
        val email=findViewById<EditText>(R.id.etLoginEmail)
        val password=findViewById<EditText>(R.id.etLoginPassword)
        val twLoginError=findViewById<TextView>(R.id.twLoginError)
        fun setTopMarginForError(){
            val params = twLoginError.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = 20.dp
        }
        if (email.text.isEmpty()){
            isValid=false
            email.setBackgroundResource(R.drawable.et_rounded_error)
            setTopMarginForError()
        }
        else{
            email.setBackgroundResource(R.drawable.et_rounded)
        }
        if (password.text.isEmpty()){
            isValid=false
            password.setBackgroundResource(R.drawable.et_rounded_error)
            setTopMarginForError()
        }
        else{
            password.setBackgroundResource(R.drawable.et_rounded)
        }
        if (!isValid){
            setTopMarginForError()
            twLoginError.text="Нисте попунили сва поља."
            return
        }
        else{
            val map = HashMap<String, String>()
            map["email"]=email.text.toString()
            map["password"]=password.text.toString()

            val call: Call<LoginResult> = retrofitInterface.executeLogin(map)

            call.enqueue(object : Callback<LoginResult> {
                override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                    if (response.code() == 200) {
                        val result: LoginResult? = response.body()
                        val token = result?.token
                        val editor = sharedPreferences.edit()
                        editor.putString("AUTH_TOKEN", token)
                        editor.putString("type",result?.role)
                        editor.apply()
                        if(result?.role=="user")
                        Intent(this@LoginActivity, SelectChildActivity::class.java).also{
                            startActivity(it)
                            finish()
                        }
                        else if(result?.role=="admin")
                            Intent(this@LoginActivity,AdminActivity::class.java).also{
                                startActivity(it)
                                finish()
                            }

                    } else if (response.code() == 404) {

                        Toast.makeText(
                            this@LoginActivity, "Нисте унели исправне податке.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity, t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }


    }

    override fun onResume() {
        super.onResume()
        clearFields()
    }
    private fun clearFields(){

        findViewById<EditText>(R.id.etLoginEmail).text.clear()
        findViewById<EditText>(R.id.etLoginPassword).text.clear()
        findViewById<TextView>(R.id.twLoginError).text=""
    }
}