package com.example.exoplayerott

import JsonReader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.exoplayerott.adapters.MyCustomExoPlayerRecyclerView
import com.example.exoplayerott.adapters.MyRecyclerAdapter
import com.example.exoplayerott.databinding.ActivityMainBinding
import com.example.exoplayerott.models.Playlist
import com.example.exoplayerott.models.SamplePlaylistResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var mRecyclerView: MyCustomExoPlayerRecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main)
        lifecycleScope.launch(Dispatchers.IO) {
            val jsonReader = JsonReader(this@MainActivity)
            val jsonString = jsonReader.loadJSONFromAsset("sample.json")
            val obj = Gson().fromJson(jsonString,SamplePlaylistResponse::class.java)
            withContext(Dispatchers.Main){
                mRecyclerView = binding.mainRecycle
                obj.playlist?.let { initRecyclerView(it) }
            }
        }
    }
    private fun initRecyclerView(list: List<Playlist>) {
        val layoutManager = LinearLayoutManager(this)
        mRecyclerView?.layoutManager = layoutManager
        mRecyclerView?.setMediaObjects(list)
        val adapter = MyRecyclerAdapter(list, initGlide()!!)
        mRecyclerView?.adapter = adapter
    }

    private fun initGlide(): RequestManager? {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.loader_2)
            .error(R.drawable.loader_2)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    override fun onDestroy() {
        mRecyclerView?.releasePlayer()
        super.onDestroy()
    }
}