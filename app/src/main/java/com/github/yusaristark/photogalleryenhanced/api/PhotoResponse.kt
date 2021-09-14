package com.github.yusaristark.photogalleryenhanced.api

import com.github.yusaristark.photogalleryenhanced.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo") lateinit var galleryItems: List<GalleryItem>
}