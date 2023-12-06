package com.example.exoplayerott.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.exoplayerott.R
import com.example.exoplayerott.models.Playlist

class MyRecyclerAdapter(
    sampleEpisodesList: List<Playlist>,
    requestManager: RequestManager
) : RecyclerView.Adapter<MyRecyclerViewHolder>() {
    private val listOfEpisodes: List<Playlist>
    private val mRequestManager: RequestManager

    init {
        listOfEpisodes = sampleEpisodesList
        mRequestManager = requestManager
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRecyclerViewHolder {
        return MyRecyclerViewHolder(
            (LayoutInflater.from(parent.context).inflate(R.layout.main_list_row, parent, false)
        ))
    }


    override fun onBindViewHolder(holder: MyRecyclerViewHolder, position: Int) {
        holder.onBind(listOfEpisodes[position], mRequestManager)
    }

    override fun getItemCount(): Int {
        return listOfEpisodes.size
    }
}