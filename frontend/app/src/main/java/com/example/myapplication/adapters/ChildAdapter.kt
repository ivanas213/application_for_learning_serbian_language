package com.example.myapplication.adapters


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.RetrofitInstance
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.models.Child
import com.example.myapplication.models.PictureResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChildAdapter(private val context: Context, private val children: List<Child>) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    private var retrofitInterface= RetrofitInstance.retrofitInterface
    private val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_child_profile, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val child = children[position]
        holder.nameTextView.text = child.name

        val imageUrl=child.image.picture
        val imageData = "data:image/png;base64," + imageUrl
        Glide.with(holder.itemView.context)
            .load(imageData)
            .into(holder.avatarImageView)


        holder.nameTextView.setOnClickListener{
            addChildToSharedPreference(child)
            Intent(context,MainActivity::class.java).also {
                context.startActivity(it)
                if (context is Activity){
                    context.finish()
                }
            }
        }
        holder.avatarImageView.setOnClickListener{
            addChildToSharedPreference(child)
            Intent(context,MainActivity::class.java).also {
                context.startActivity(it)
                if (context is Activity){
                    context.finish()
                }
            }
        }



    }

    override fun getItemCount(): Int {
        return children.size
    }

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvName)
        val avatarImageView: ImageView = itemView.findViewById(R.id.ivAvatar)
    }
    fun addChildToSharedPreference(child:Child){
        val gson = Gson()
        val childJson = gson.toJson(child)

        with(sharedPref.edit()) {
            putString("child", childJson)
            apply()
        }
    }
}