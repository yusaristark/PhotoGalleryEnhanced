package com.github.yusaristark.photogalleryenhanced

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val flickrFetcher: FlickrFetcher

    init {
        flickrFetcher = FlickrFetcher()
        galleryItemLiveData = flickrFetcher.fetchPhotos()
    }

    //отмена текущего запроса (если такой есть) при завершении приложения
    override fun onCleared() {
        super.onCleared()
        flickrFetcher.cancelRequestInFlight()
    }
}