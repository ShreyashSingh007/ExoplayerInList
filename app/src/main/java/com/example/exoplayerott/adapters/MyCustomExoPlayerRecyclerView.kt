package com.example.exoplayerott.adapters

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.exoplayerott.R
import com.example.exoplayerott.models.Playlist
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MyCustomExoPlayerRecyclerView : RecyclerView {
    private enum class VolumeState {
        ON, OFF
    }
    private var thumbnail: ImageView? = null
    private var volumeControl: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var frameLayout: FrameLayout? = null
    private var videoSurfaceView: PlayerView? = null
    private var videoPlayer: SimpleExoPlayer? = null
    private var mediaObjects: List<Playlist>? = null
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var context: Context? = null
    private var playPosition = -1
    private var isVideoViewAdded = false
    private var requestManager: RequestManager? = null
    private var volumeState: VolumeState? = null
    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }
    private fun init(context: Context) {
        this.context = context.applicationContext
        val display =
            (getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y
        videoSurfaceView = PlayerView(context)
        videoSurfaceView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        videoPlayer = SimpleExoPlayer.Builder(context).build()
        videoSurfaceView!!.useController = false
        videoSurfaceView!!.player = videoPlayer
        setVolumeControl(VolumeState.ON)
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    if (thumbnail != null) { // show the old thumbnail
                        thumbnail!!.visibility = VISIBLE
                    }
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true)
                    } else {
                        playVideo(false)
                    }
                }
            }
        })
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent == view) {
                    resetVideoView()
                }
            }
        })
        videoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        if (progressBar != null) {
                            progressBar!!.visibility = VISIBLE
                        }
                    }

                    Player.STATE_ENDED -> {
                        videoPlayer!!.seekTo(0)
                    }

                    Player.STATE_IDLE -> {}
                    Player.STATE_READY -> {
                        if (progressBar != null) {
                            progressBar!!.visibility = GONE
                        }
                        if (!isVideoViewAdded) {
                            addVideoView()
                        }
                    }
                    else -> {}
                }
            }
        })

    }
    fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int
        if (!isEndOfList) {
            val startPosition =
                (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            var endPosition =
                (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }
            if (startPosition < 0 || endPosition < 0) {
                return
            }
            targetPosition = if (startPosition != endPosition) {
                val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
                if (startPositionVideoHeight > endPositionVideoHeight) startPosition else endPosition
            } else {
                startPosition
            }
        } else {
            targetPosition = mediaObjects?.size?.minus(1) ?: 0
        }
        if (targetPosition == playPosition) {
            return
        }
        playPosition = targetPosition
        if (videoSurfaceView == null) {
            return
        }
        videoSurfaceView?.visibility = INVISIBLE
        removeVideoView(videoSurfaceView)
        val currentPosition =
            targetPosition - (layoutManager as LinearLayoutManager?)?.findFirstVisibleItemPosition()!!
        val child = getChildAt(currentPosition) ?: return
        val holder = child.tag as? MyRecyclerViewHolder
        if (holder == null) {
            playPosition = -1
            return
        }
        thumbnail = holder.thumbnail
        progressBar = holder.progressBar
        volumeControl = holder.volumeControl
        viewHolderParent = holder.itemView
        requestManager = holder.requestManager
        frameLayout = holder.itemView.findViewById<FrameLayout>(R.id.media_container)
        videoSurfaceView!!.player = videoPlayer
        viewHolderParent!!.setOnClickListener(videoViewClickListener)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context!!, Util.getUserAgent(context!!, "MyInstagramOTTApp")
        )
        var mediaUrl = ""
        for (item in mediaObjects!![targetPosition].sources!!){
            if (!item.label.isNullOrBlank()){
                if (item.label == "180p" ){
                    mediaUrl = item.file.toString()
                }
            }
        }
        if (mediaUrl.isEmpty()){
            mediaUrl = mediaObjects?.get(targetPosition)?.sources?.get(1)?.file.toString()
        }
        val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(mediaUrl))
        videoPlayer!!.setMediaSource(videoSource)
        videoPlayer!!.prepare()
        videoPlayer!!.playWhenReady = true
    }

    private val videoViewClickListener = OnClickListener { toggleVolume() }
    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at =
            playPosition - (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        val child = getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }
    private fun removeVideoView(videoView: PlayerView?) {
        if (videoView != null) {
            val parent = videoView.parent as ViewGroup?
            parent?.let {
                val index = parent.indexOfChild(videoView)
                if (index >= 0) {
                    parent.removeViewAt(index)
                    isVideoViewAdded = false
                    viewHolderParent?.setOnClickListener(null)
                }
            }
        }
    }
    private fun addVideoView() {
        frameLayout!!.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView!!.requestFocus()
        videoSurfaceView!!.visibility = VISIBLE
        videoSurfaceView!!.alpha = 1f
        thumbnail!!.visibility = GONE
    }
    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            playPosition = -1
            videoSurfaceView!!.visibility = INVISIBLE
            thumbnail!!.visibility = VISIBLE
        }
    }
    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
        viewHolderParent = null
    }

    private fun toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                setVolumeControl(VolumeState.OFF)
            }
        }
    }

    private fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            videoPlayer!!.volume = 0f
            animateVolumeControl()
        } else if (state == VolumeState.ON) {
            videoPlayer!!.volume = 1f
            animateVolumeControl()
        }
    }

    private fun animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl!!.bringToFront()
            if (volumeState == VolumeState.OFF) {
                requestManager?.load(R.drawable.ic_volume_off_black_24dp)
                    ?.into(volumeControl!!)
            } else if (volumeState == VolumeState.ON) {
                requestManager?.load(R.drawable.ic_volume_up_black_24dp)
                    ?.into(volumeControl!!)
            }
            volumeControl!!.animate().cancel()
            volumeControl!!.alpha = 1f
            volumeControl!!.animate()
                .alpha(0f)
                .setDuration(600).startDelay = 1000
        }
    }

    fun setMediaObjects(mediaObjects: List<Playlist>) {
        this.mediaObjects = mediaObjects
    }
}