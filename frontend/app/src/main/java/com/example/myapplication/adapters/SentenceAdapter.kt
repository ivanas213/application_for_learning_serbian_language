package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.SentencePart

class SentenceAdapter(private val sentenceParts: List<SentencePart>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_EDITABLE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (sentenceParts[position].isEditable) TYPE_EDITABLE else TYPE_TEXT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_TEXT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_text_view_wc, parent, false)
            TextViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_text, parent, false)
            EditTextHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val part = sentenceParts[position]
        if (holder is TextViewHolder) {
            holder.textView.text = part.content
        } else if (holder is EditTextHolder) {
            holder.editText.hint = ""
        }
    }

    override fun getItemCount(): Int = sentenceParts.size

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tw)
    }

    class EditTextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText = itemView.findViewById(R.id.et)
    }
}
