package com.example.myapplication.viewHolders

import TextBlock
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat

class TextViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.textView)

    fun bind(textBlock: TextBlock) {
        val modifiedContent = textBlock.content.replace("<double_u>", "<u><u>")
            .replace("</double_u>", "</u></u>")

        val content: Spanned = Html.fromHtml(modifiedContent, Html.FROM_HTML_MODE_LEGACY)

        textView.text = content
        if(!isTablet(context))
        textView.textSize = textBlock.style.fontSize.toFloat()
        else
            textView.textSize=textBlock.style.fontSize.toFloat()*3/2
        if (textBlock.style.color != "#000000") {
            textView.setBackgroundColor(Color.parseColor(textBlock.style.color))
        } else {
            textView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        textView.setTypeface(
            textView.typeface,
            when {
                textBlock.style.bold && textBlock.style.italic -> Typeface.BOLD_ITALIC
                textBlock.style.bold -> Typeface.BOLD
                textBlock.style.italic -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
        )
        val typeface = ResourcesCompat.getFont(textView.context, R.font.rubik)
        textView.typeface=typeface

        when (textBlock.style.alignment) {
            "center" -> textView.gravity = Gravity.CENTER
            "left" -> textView.gravity = Gravity.START
            "right" -> textView.gravity = Gravity.END
            else -> textView.gravity = Gravity.START
        }
    }
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

}
