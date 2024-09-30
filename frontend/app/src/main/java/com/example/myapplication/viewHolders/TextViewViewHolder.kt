package com.example.myapplication.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class TextViewViewHolder(view: View) : RecyclerView.ViewHolder(view){
    private val twLessonTitle=view.findViewById<TextView>(R.id.twLessonTitle)
    fun bind(title:String){
        twLessonTitle.text=title
    }

}