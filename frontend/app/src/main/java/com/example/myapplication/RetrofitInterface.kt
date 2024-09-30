package com.example.myapplication

import Lesson
import QuestionBlock
import com.example.myapplication.models.Avatar
import com.example.myapplication.models.Child
import com.example.myapplication.models.LessonTitle
import com.example.myapplication.models.LoginResult
import com.example.myapplication.models.MessageResult
import com.example.myapplication.models.PictureResponse
import com.example.myapplication.models.RegisterResult
import com.example.myapplication.models.Topic
import com.example.myapplication.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitInterface {

    @POST("user/login")
    fun executeLogin(@Body map: HashMap<String, String>): Call<LoginResult>

    @POST("user/register")
    fun executeRegister(@Body map: HashMap<String, String>): Call<RegisterResult>

    @GET("/verify/{token}")
    fun verifyEmail(@Path("token") token: String): Call<Void>

    @POST("/user/forgottenPassword")
    fun forgottenPassword(@Body map: HashMap<String,String>):Call<MessageResult>

    @GET("lesson/grade/{grade}")
    fun getLessonsByGrade(@Path("grade") grade: Int): Call<List<Lesson>>

    @GET("avatar/getAvatars")
    fun getAvatars(): Call<List<Avatar>>

    @GET("user/getChildren")
    fun getChildren(
        @Header("Authorization") token: String
    ): Call<List<Child>>

    @POST("user/addChild")
    fun addChild(@Header("Authorization") token: String, @Body map: HashMap<String,String>):Call<Child>

    @GET("/avatar/{id}/picture")
    fun getPictureByID(@Path("id") id:String):Call<PictureResponse>

    @GET("/topic/getAllTopics")
    fun getAllTopics():Call<Topic>

    @GET("/topic/getTopics/{grade}")
    fun getTopics(@Header("Authorization") token: String,@Path("grade") grade:Int):Call<List<Topic>>

    @GET("/lesson/getLessons/{topicId}")
    fun getLessonsByTopic(@Header("Authorization") token: String,@Path("topicId") topicId:String):Call<List<Lesson>>

    @GET("/lesson/getLessonTitles/{topicId}")
    fun getLessonTitlesByTopic(@Header("Authorization") token: String,@Path("topicId") topicId:String):Call<List<LessonTitle>>

    @PUT("/user/updateGrade/{childId}")
    fun updateChildGrade(
        @Header("Authorization") token: String,
        @Path("childId") childId: String,
        @Body map: HashMap<String, Int>
    ): Call<MessageResult>

    @POST("/lesson/question/{id}/correct_answer")
    fun correctAnswer(@Header("Authorization") token: String,@Path("id") id:String, @Body map: HashMap<String,Int>):Call<MessageResult>

    @POST("/lesson/child/addLesson/{childId}")
    fun completedLesson(
        @Header("Authorization") token: String,
        @Path("childId") childId: String,
        @Body map: HashMap<String, String>
    ): Call<MessageResult>
    @GET("/user/child/{childId}")
    fun getChildById(@Header("Authorization") token: String,@Path("childId") childId:String):Call<Child>

    @GET("/lesson/getLessonById/{lessonId}")
    fun getLessonById(@Header("Authorization") token: String,@Path("lessonId") lessonId:String):Call<Lesson>

    @GET("/topic/random_questions_by_topic/{topicId}")
    fun getTestQuestions(@Header("Authorization") token: String,@Path("topicId") topicId:String):Call<List<QuestionBlock>>

    @POST("user/child/addTestResult/{childId}")
    fun completedTest(
        @Header("Authorization") token: String,
        @Path("childId") childId: String,
        @Body map: HashMap<String, String>
    ): Call<MessageResult>

    @GET("user/users")
    fun getUsers(@Header("Authorization") token: String):Call<List<User>>
}

