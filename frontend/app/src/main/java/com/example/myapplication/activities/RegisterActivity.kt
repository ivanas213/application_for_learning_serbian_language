package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

class RegisterActivity : AppCompatActivity() {
    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val authToken = sharedPreferences.getString("AUTH_TOKEN", null)
        if (authToken != null) {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
        clear()

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        val registerButton=findViewById<Button>(R.id.btnRegister)
        val logTW=findViewById<TextView>(R.id.twRegisterToLogin)
        val srcEyePassword=findViewById<ImageView>(R.id.srcEyeRegisterPassword)
        val srcEyePasswordAgain=findViewById<ImageView>(R.id.srcEyeRegisterPasswordAgain)
        val etRegisterPassword=findViewById<EditText>(R.id.etRegisterPassword)
        val etRegisterPasswordAgain=findViewById<EditText>(R.id.etRegisterPasswordAgain)


        registerButton.setOnClickListener{
            handleRegister()
        }
        logTW.setOnClickListener{
            Intent(this, LoginActivity::class.java).also{
                startActivity(it)
            }
        }
        var isPasswordVisible = false
        var isPasswordAgainVisible = false
        srcEyePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                srcEyePassword.setImageResource(R.drawable.eye)
            } else {
                etRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                srcEyePassword.setImageResource(R.drawable.eyex)
            }
            etRegisterPassword.setSelection(etRegisterPassword.text.length)
        }
        srcEyePasswordAgain.setOnClickListener {
            isPasswordAgainVisible = !isPasswordAgainVisible
            if (isPasswordAgainVisible) {
                etRegisterPasswordAgain.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                srcEyePasswordAgain.setImageResource(R.drawable.eye)
            } else {
                etRegisterPasswordAgain.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                srcEyePasswordAgain.setImageResource(R.drawable.eyex)
            }
            etRegisterPasswordAgain.setSelection(etRegisterPasswordAgain.text.length)
        }




}

    override fun onResume() {
        super.onResume()
        clear()
    }
    private fun handleRegister() {

        val email=findViewById<EditText>(R.id.etRegisterEmail)
        val password=findViewById<EditText>(R.id.etRegisterPassword)
        val passwordAgain=findViewById<EditText>(R.id.etRegisterPasswordAgain)
        val map=HashMap<String,String>()
        val twError=findViewById<TextView>(R.id.twRegisterError)
        fun setTopMarginForError(){
            val params = twError.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = 20.dp
        }
        var isValid=true
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
        if (passwordAgain.text.isEmpty()){
            isValid=false
            passwordAgain.setBackgroundResource(R.drawable.et_rounded_error)
            setTopMarginForError()
        }
        else{
            passwordAgain.setBackgroundResource(R.drawable.et_rounded)
        }

        if (!isValid ){
            setTopMarginForError()
            twError.text="Niste popunili sva polja"
            return
        }

        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        val emailMatcher = Regex(emailPattern).matchEntire(email.text.toString())
        if (emailMatcher == null) {
            email.setBackgroundResource(R.drawable.et_rounded_error)
            setTopMarginForError()
            twError.text = "Neispravan format email adrese."
            return
        }
        val passwordString=password.text.toString()
        if (passwordString.length<8){
            setTopMarginForError()
            twError.text = "Lozinka mora sadržati barem 8 karaktera."
            password.setBackgroundResource(R.drawable.et_rounded_error)
            return
        }
        if(passwordString.none{ it.isUpperCase() }){
            setTopMarginForError()
            twError.text = "Lozinka mora sadržati barem jedno veliko slovo."
            password.setBackgroundResource(R.drawable.et_rounded_error)
            return
        }
        if(passwordString.none{ it.isDigit() }){
            setTopMarginForError()
            twError.text = "Lozinka mora sadržati barem jedan broj."
            password.setBackgroundResource(R.drawable.et_rounded_error)
            return
        }
        if (password.text.toString()!=passwordAgain.text.toString()){
            password.setBackgroundResource(R.drawable.et_rounded_error)
            passwordAgain.setBackgroundResource(R.drawable.et_rounded_error)
            twError.text="Lozinke se ne poklapaju."
            setTopMarginForError()
            return
        }
        map["email"]=email.text.toString()
        map["password"]=password.text.toString()
        val call: Call<RegisterResult> = retrofitInterface.executeRegister(map)

        call.enqueue(object : ErrorHandlingCallback<RegisterResult>() {
            override fun onSuccess(call: Call<RegisterResult>, response: Response<RegisterResult>) {
                val result: RegisterResult? = response.body()


                val intent = Intent(this@RegisterActivity, VerificationActivity::class.java)
                intent.putExtra("email", email.text.toString())
                intent.putExtra("password", password.text.toString())
                startActivity(intent)

            }

            override fun onError(call: Call<RegisterResult>, errorResponse: MessageResult) {
                val twError = findViewById<TextView>(R.id.twRegisterError)
                twError.text = errorResponse.getMessage()
            }
        })




    }
    private fun clear(){

        findViewById<EditText>(R.id.etRegisterEmail).text.clear()
        findViewById<EditText>(R.id.etRegisterPassword).text.clear()
        findViewById<EditText>(R.id.etRegisterPasswordAgain).text.clear()
        findViewById<TextView>(R.id.twRegisterError).text = ""
    }
}