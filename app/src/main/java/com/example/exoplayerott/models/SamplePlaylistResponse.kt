package com.example.exoplayerott.models


data class SamplePlaylistResponse(
    val title: String? = null,
    val description: String? = null,
    val kind: String? = null,
    val feedid: String? = null,
    val links: Links? = null,
    val playlist: List<Playlist>? = null,
    val feedInstanceId: String? = null,
)

data class Links(
    val first: String?,
    val last: String?,
)

data class Playlist(
    var title: String?,
    val mediaid: String?,
    val link: String?,
    val image: String?,
    val images: List<Image>?,
    val feedid: String?,
    val duration: Long?,
    val pubdate: Long?,
    val description: String?,
    val tags: String?,
    val sources: List<Source>?,
    val tracks: List<Track>?,
    val variations: Map<String, Any>?,
    val episodeNumber: String?,
    val rating: String?,
    val seasonNumber: String?,
    val seriesRating: String?,
    var seriesTitle: String?=null,
    val iconikAssetId: String?,
    val free: String?,
    val episodeTrt: String?,
    val smpteAds: String?,
    val seriesEnd: String?,
    val seriesPremiere: String?,
    val smpteAds2: String?,
    var arrSeriesList: ArrayList<Playlist>?,
    var isExpanded: Boolean = false,
    var inEditMode: Boolean = false,
    var isChecked: Boolean = false,
    var isInMyList:Boolean = false,
    var isDownloaded :Boolean = false,
    var seasonCount: Int? = null,
)

data class Image(
    val src: String?,
    val width: Long?,
    val type: String?,
)

data class Source(
    val file: String?,
    val type: String?,
    val height: Long?,
    val width: Long?,
    val label: String?,
    val bitrate: Long?,
    val filesize: Long?,
    val framerate: Double?,
)

data class Track(
    val file: String?,
    val kind: String?,
    val label: String?,
    val includedInManifest: Boolean?,
)
