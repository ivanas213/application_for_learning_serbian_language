package com.example.myapplication

import com.example.myapplication.models.MessageResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson

abstract class ErrorHandlingCallback<T> : Callback<T> {

    abstract fun onSuccess(call: Call<T>, response: Response<T>)
    abstract fun onError(call: Call<T>, errorResponse: MessageResult)

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(call, response)
        } else {
            val errorBody = response.errorBody()?.string()
            val gson = Gson()
            val errorResponse = gson.fromJson(errorBody, MessageResult::class.java)
            onError(call, errorResponse)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        t.printStackTrace()
    }
}
