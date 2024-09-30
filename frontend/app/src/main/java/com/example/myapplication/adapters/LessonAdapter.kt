package com.example.myapplication.adapters

import ContentBlock
import ImageViewHolder
import QuestionBlock
import TableViewHolder
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.viewHolders.FooterViewHolder
import com.example.myapplication.viewHolders.TextViewHolder
import com.example.myapplication.viewHolders.TextViewViewHolder
import com.example.myapplication.viewHolders.VideoViewHolder


class LessonAdapter(
    private var contentBlocks: List<ContentBlock>,
    private val recyclerView: RecyclerView ,
    private val context:Context,
    private val title:String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var questions: MutableList<QuestionBlock> = mutableListOf()

    companion object {
        private const val TYPE_TEXT = 1
        private const val TYPE_IMAGE = 2
        private const val TYPE_QUESTION = 3
        private const val TYPE_BUTTON = 4
        private const val TYPE_TABLE=5
        private const val TYPE_VIDEO=6
        private const val TYPE_TEXT_VIEW=7
    }

    fun getQuestions(): List<QuestionBlock> {
        return questions
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == contentBlocks.size+1 || position==0) {
            if(position==contentBlocks.size+1) TYPE_BUTTON
            else TYPE_TEXT_VIEW
        } else {
            when (contentBlocks[position-1].type) {
                "text" -> TYPE_TEXT
                "image" -> TYPE_IMAGE
                "question" -> {
                    contentBlocks[position-1].questionBlock?.let { questionBlock ->
                        if (!questions.contains(questionBlock) && questionBlock.questionType!="fill") {
                            questions.add(questionBlock)
                        }
                    }
                    TYPE_QUESTION
                }
                "table"->{
                    TYPE_TABLE
                }
                "video"->{
                    TYPE_VIDEO
                }
                else -> throw IllegalArgumentException("Unknown content type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEXT -> TextViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_block, parent, false),parent.context
            )
            TYPE_IMAGE -> ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_block, parent, false)
            )
            TYPE_QUESTION -> object : RecyclerView.ViewHolder(View(parent.context)) {}

            TYPE_BUTTON ->
                FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.footer_view,parent,false),this@LessonAdapter)

            TYPE_TEXT_VIEW->TextViewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_text_view,parent,false))
            TYPE_TABLE -> TableViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_table, parent, false)
            )
            TYPE_VIDEO-> VideoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_video, parent, false),context
            )
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_TEXT -> (holder as TextViewHolder).bind(contentBlocks[position-1].textBlock!!)
            TYPE_IMAGE -> (holder as ImageViewHolder).bind(contentBlocks[position-1].imageBlock!!)
            TYPE_QUESTION -> {
            }

            TYPE_BUTTON -> {
                val recyclerViewHeight = recyclerView.height
                (holder as FooterViewHolder).bind(recyclerViewHeight)
            }
            TYPE_TEXT_VIEW->(holder as TextViewViewHolder).bind(title)

            TYPE_TABLE -> (holder as TableViewHolder).bind(contentBlocks[position-1].tableBlock!!)
            TYPE_VIDEO -> {
                (holder as VideoViewHolder).bind(contentBlocks[position-1].videoBlock!!)
            }
        }
    }

    override fun getItemCount(): Int = contentBlocks.size + 2

    @SuppressLint("NotifyDataSetChanged")
    fun setNewLesson(newContentBlocks: List<ContentBlock>) {
        this.contentBlocks = newContentBlocks
        questions.clear()
        notifyDataSetChanged()
    }
}



