package com.example.myapplication.models

import Lesson
import java.util.Date

data class Child(
    val _id:String,
    val name: String,
    val birthdate: Date?,
    val image: Avatar,
    val progress: Progress?,
    val selectedGrade :Int
)

data class Progress(
    var completed_lessons: List<Lesson>,
    val test_results: List<TestResult>
)

data class TestResult(
    val topicId: Topic,
    val result: Int
)
data class User(
    val email:String,
    val children:List<Child>
)