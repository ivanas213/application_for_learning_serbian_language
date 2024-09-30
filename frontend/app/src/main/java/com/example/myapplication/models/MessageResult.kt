package com.example.myapplication.models

data class MessageResult(private val message:String?) {
    fun getMessage():String?{
        return message
    }
}