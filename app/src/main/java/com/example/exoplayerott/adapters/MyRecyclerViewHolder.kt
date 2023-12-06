package com.example.exoplayerott.adapters

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.exoplayerott.R
import com.example.exoplayerott.models.Playlist

class MyRecyclerViewHolder(parent: View) : RecyclerView.ViewHolder(
    parent
) {
    var title: TextView = itemView.findViewById(R.id.title)
    var thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
    var volumeControl: ImageView = itemView.findViewById(R.id.volume_control)
    var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    var requestManager: RequestManager? = null
    fun onBind(mediaObject: Playlist, requestManager: RequestManager?) {
        itemView.tag = this
        this.requestManager = requestManager
        title.text = mediaObject.title
        this.requestManager
            ?.load(mediaObject.image)
            ?.into(thumbnail)
    }
}