package com.example.myapplication.adapters

import  android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Child

class UserChildAdapter(private val children:List<Child>, private val context:Context) : RecyclerView.Adapter<UserChildAdapter.UserChildViewHolder>() {
    inner class UserChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childImage:ImageView = itemView.findViewById(R.id.image)
        val childName:TextView=itemView.findViewById(R.id.child)
        val lessonContainer: RecyclerView = itemView.findViewById(R.id.lessonContainer)
        val testContainer: RecyclerView = itemView.findViewById(R.id.testContainer)
        val lessons:TextView=itemView.findViewById(R.id.lessons)
        val tests:TextView=itemView.findViewById(R.id.tests)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_child, parent, false)
        return UserChildViewHolder(view)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: UserChildViewHolder, position: Int) {
        holder.childName.text=children[position].name
        val base64Image = children[position].image.picture

        val decodedString: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)

        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        if (decodedByte != null) {
            holder.childImage.setImageBitmap(decodedByte)
        }
        holder.lessonContainer.layoutManager=LinearLayoutManager(context)
        holder.lessonContainer.adapter= children[position].progress?.let { ChildLessonAdapter(it.completed_lessons) }
        holder.testContainer.layoutManager=LinearLayoutManager(context)
        holder.testContainer.adapter= children[position].progress?.let { ChildTestAdapter(it.test_results) }
        holder.childImage.setOnClickListener{
            toggleVisibility(holder.testContainer)
            toggleVisibility(holder.lessonContainer)
            if(holder.testContainer.visibility==View.VISIBLE){
                if(children[position].progress?.completed_lessons?.size!! >0){
                    holder.lessons.visibility=View.VISIBLE
                }
                if(children[position].progress?.test_results?.size!! >0){
                    holder.tests.visibility=View.VISIBLE
                }
            }
            else{
                holder.lessons.visibility=View.GONE
                holder.tests.visibility=View.GONE
            }
        }
        holder.childName.setOnClickListener{
            toggleVisibility(holder.testContainer)
            toggleVisibility(holder.lessonContainer)
            if(holder.testContainer.visibility==View.VISIBLE){
                if(children[position].progress?.completed_lessons?.size!! >0){
                    holder.lessons.visibility=View.VISIBLE
                }
                if(children[position].progress?.test_results?.size!! >0){
                    holder.tests.visibility=View.VISIBLE
                }
            }
            else{
                holder.lessons.visibility=View.GONE
                holder.tests.visibility=View.GONE
            }
        }
    }
    private fun toggleVisibility(view: View) {

        view.visibility = if (view.visibility == View.GONE) View.VISIBLE else View.GONE
    }
}