package com.example.myapplication.data.dao

import Lesson
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE topic = :topicId")
    suspend fun getLessonsByTopic(topicId: String): List<Lesson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Delete
    suspend fun deleteLesson(lesson: Lesson)
}