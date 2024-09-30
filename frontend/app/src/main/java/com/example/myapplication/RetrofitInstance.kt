package com.example.myapplication

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {
    private val cookieJar = object : CookieJar {
        private val cookieStore: MutableMap<HttpUrl, List<Cookie>> = HashMap()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url] ?: ArrayList()
        }
    }
    val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            var request = chain.request()
            var response = chain.proceed(request)
            var tryCount = 0
            val maxTries = 3

            while (!response.isSuccessful && tryCount < maxTries) {
                tryCount++
                response = chain.proceed(request)
            }
            response
        }
        .build()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.SERVER_IP_ADRESS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val retrofitInterface: RetrofitInterface by lazy {
        retrofit.create(RetrofitInterface::class.java)
    }
}
