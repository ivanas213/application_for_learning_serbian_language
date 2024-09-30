package com.example.myapplication.viewHolders

import VideoBlock
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R



class VideoViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val webView: WebView = itemView.findViewById(R.id.videoWebView)

    fun bind(video: VideoBlock?) {
        val screenWidth = getScreenWidth(context)

        val videoHtml = """
            <html>
            <body style="margin:0;padding:0;display:flex;justify-content:center;align-items:center;">
            <div style="position:relative;width:100%;max-width:${screenWidth}px;aspect-ratio:16/9;">
                <iframe src="${video?.url}" style="position:absolute;top:0;left:0;width:100%;height:100%;" 
                        frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                        allowfullscreen></iframe>
            </div>
            </body>
            </html>
        """

        webView.settings.javaScriptEnabled = true
        webView.loadData(videoHtml, "text/html", "utf-8")
    }

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
            val bounds = windowMetrics.bounds
            bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}


