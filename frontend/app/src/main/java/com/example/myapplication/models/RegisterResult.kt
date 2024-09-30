package com.example.myapplication.models

data class RegisterResult(private val token: String, private val message:String){
    fun getToken(): String {
        return token
    }
    fun getMessage():String{
        return message
    }


}
