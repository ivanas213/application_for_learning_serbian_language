package com.example.myapplication.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.Avatar




class AvatarAdapter(
    private val avatars: List<Avatar>,
    private val onAvatarSelected: (Avatar) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var selectedAvatar: Avatar? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatar = avatars[position]

        Glide.with(holder.itemView.context)
            .load("data:image/png;base64," + avatar.picture)
            .into(holder.avatarImageView)

        holder.itemView.setBackgroundResource(
            if (avatar == selectedAvatar) R.drawable.avatar_selected_background
            else R.drawable.avatar_default_background
        )

        holder.itemView.setOnClickListener {
            selectedAvatar = avatar
            notifyDataSetChanged()
            onAvatarSelected(avatar)
        }
    }

    override fun getItemCount(): Int = avatars.size

    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.iwAvatar)
    }
}
