package com.example.myapplication.adapters

import Lesson
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ChildLessonAdapter(val lessons:List<Lesson>): RecyclerView.Adapter<ChildLessonAdapter.ChildLessonViewHolder>()  {
    inner class ChildLessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val lesson=itemView.findViewById<TextView>(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildLessonViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_lesson_result,parent,false)
        return ChildLessonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

    override fun onBindViewHolder(holder: ChildLessonViewHolder, position: Int) {
        holder.lesson.text=lessons[position].title
    }
}