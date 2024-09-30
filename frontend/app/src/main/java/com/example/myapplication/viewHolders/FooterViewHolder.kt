package com.example.myapplication.viewHolders

import Lesson
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.QuestionsActivity
import com.example.myapplication.adapters.LessonAdapter
import com.google.gson.Gson

class FooterViewHolder(itemView: View, val adapter: LessonAdapter) : RecyclerView.ViewHolder(itemView) {
    val footerSpacer: View = itemView.findViewById(R.id.footerSpacer)
    val btnGoToQuestions: Button = itemView.findViewById(R.id.btnGoToQuestions)
    fun bind(recyclerViewHeight: Int) {
        itemView.post {
            val itemViewHeight = itemView.height
            val itemViewY = itemView.y


            val spaceBelow = recyclerViewHeight - (itemViewY + itemViewHeight)

            if (spaceBelow > 0) {
                val layoutParams = footerSpacer.layoutParams
                layoutParams.height = spaceBelow.toInt()
                footerSpacer.layoutParams = layoutParams
            } else {
                val layoutParams = footerSpacer.layoutParams
                layoutParams.height = 0
                footerSpacer.layoutParams = layoutParams
            }
        }
        btnGoToQuestions.setOnClickListener {
            saveQuestionsToSharedPreferences(it.context)
            val intent = Intent(it.context, QuestionsActivity::class.java)
            intent.putExtra("isTest",false)
            it.context.startActivity(intent)
            (it.context as Activity).finish()
        }
    }



    private fun saveQuestionsToSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(adapter?.getQuestions())
        val id = getCurrentLesson(context)?._id
        editor.putString("questions$id", json)
        editor.apply()

    }
    fun getCurrentLesson(context: Context): Lesson? {
        val sharedPreferences = context.getSharedPreferences("user_session", MODE_PRIVATE)

        val json = sharedPreferences.getString("current_lesson", null)
        return if (json != null) {
            Gson().fromJson(json, Lesson::class.java)
        } else {
            null
        }
    }
}