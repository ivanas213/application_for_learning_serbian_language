package com.example.myapplication.adapters

import Lesson
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.HangmanActivity
import com.example.myapplication.activities.LessonActivity
import com.example.myapplication.activities.QuestionsActivity
import com.example.myapplication.models.Child
import com.example.myapplication.models.LessonTitle
import com.google.gson.Gson

class LessonTopicAdapter(private var lessons: List<LessonTitle>, private val topicType: String, private val context: Context,private val topic:String) :
    RecyclerView.Adapter<LessonTopicAdapter.LessonViewHolder>() {
    private lateinit var sharedPreferences: SharedPreferences

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.lessonIcon)
        val name: TextView = itemView.findViewById(R.id.lessonName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        sharedPreferences=context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }
    private fun getChild(): Child? {
        val json = context.getSharedPreferences("user_session", Context.MODE_PRIVATE).getString("child", null)
        return if (json != null) {
            Gson().fromJson(json, Child::class.java)
        } else {
            null
        }
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        if (position<lessons.size) {
            val lesson = lessons[position]
            val child = getChild()
            holder.itemView.setBackgroundResource(
                when (topicType) {
                    "граматика" -> R.drawable.clouds
                    "правопис" -> R.drawable.leaves
                    else -> R.drawable.crvenapoz
                }
            )
            Log.d("Moje poruke DETEEEEE", child.toString())
            if (lesson.type == "игрица") {
                holder.icon.setImageResource(R.drawable.ic_hangman)
            } else if (lesson.type == "лекција" && child?.progress != null && child.progress.completed_lessons != null) {
                var found = false
                for (l in child.progress.completed_lessons) {
                    if (l._id == lesson._id) {
                        found = true
                        break
                    }
                }
                if (found) {
                    holder.icon.setImageResource(
                        when (topicType) {
                            "граматика" -> R.drawable.ic_book_second
                            "правопис" -> R.drawable.ic_feather_second
                            else -> R.drawable.ic_lit
                        }
                    )

                } else {

                    holder.icon.setImageResource(
                        when (topicType) {
                            "граматика" -> R.drawable.ic_book
                            "правопис" -> R.drawable.ic_feather
                            else -> R.drawable.ic_lit2
                        }
                    )
                }

            }



                holder.itemView.background.alpha = 32
                holder.name.text = lesson.title

                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

                holder.itemView.layoutParams = layoutParams

                if (lessons[position].type == "лекција")
                    holder.itemView.setOnClickListener {
                        with(
                            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                .edit()
                        ) {
                            putString("current_lesson_id", lesson._id)
                            apply()
                        }
                        Intent(context, LessonActivity::class.java).also {
                            context.startActivity(it)
                        }
                    }
                else if (lessons[position].type == "игрица") {
                    Log.d("Moje poruke","usli smo u hangman 1")
                    holder.itemView.setOnClickListener {
                        Log.d("Moje poruke","usli smo u hangman 2")
                        Intent(context, HangmanActivity::class.java).also {
                            with(
                                context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                    .edit()
                            ) {
                                putString("current_lesson_id", lesson._id)
                                apply()
                            }
                            context.startActivity(it)
                        }
                    }

                }
        }
        else{
            var result=-1
            for(testResult in getChild()?.progress?.test_results!!){
                if (testResult.topicId._id==topic){
                    result=testResult.result
                    break
                }
            }
            if(result==-1)
            holder.icon.setImageResource(R.drawable.ic_test)
            else if(result<40){
                holder.icon.setImageResource(R.drawable.ic_red)
            }
            else if(result<60){
                holder.icon.setImageResource(R.drawable.ic_orange)
            }
            else if(result<80){
                holder.icon.setImageResource(R.drawable.ic_yellow)
            }
            else if(result<90){
                holder.icon.setImageResource(R.drawable.ic_green_light)
            }
            else{
                holder.icon.setImageResource(R.drawable.ic_green)
            }
            holder.itemView.setBackgroundResource(when(topicType){
                "граматика" -> R.drawable.clouds
                "правопис" ->   R.drawable.leaves
                else->R.drawable.crvenapoz
            })
            holder.itemView.background.alpha = 32
            if(result==-1)
                holder.name.text = "Тест"
            else{
                holder.name.text="Тест\n($result)"
            }
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

            holder.itemView.layoutParams = layoutParams
            holder.itemView.setOnClickListener{
                val intent=Intent(context,QuestionsActivity::class.java)
                intent.putExtra("isTest",true)
                intent.putExtra("topic",topic)
                context.startActivity(intent)
            }
        }
    }


    override fun getItemCount(): Int = lessons.size+1



}
