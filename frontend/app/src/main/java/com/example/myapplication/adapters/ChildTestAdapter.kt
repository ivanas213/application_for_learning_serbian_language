package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.TestResult

class ChildTestAdapter(val tests:List<TestResult>): RecyclerView.Adapter<ChildTestAdapter.ChildTestViewHolder>() {
    inner class ChildTestViewHolder(val itemView: View):RecyclerView.ViewHolder(itemView){
        val test=itemView.findViewById<TextView>(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildTestViewHolder {
        val view=
            LayoutInflater.from(parent.context).inflate(R.layout.item_lesson_result,parent,false)
        return ChildTestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tests.size
    }

    override fun onBindViewHolder(holder: ChildTestViewHolder, position: Int) {
        holder.test.text=tests[position].topicId.name+" ("+tests[position].result+" )"
    }
}