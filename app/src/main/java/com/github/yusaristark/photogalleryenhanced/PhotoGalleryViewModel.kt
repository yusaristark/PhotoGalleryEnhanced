package com.github.yusaristark.photogalleryenhanced

import android.app.Application
import androidx.lifecycle.*

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val flickrFetcher: FlickrFetcher = FlickrFetcher()
    private val mutableSearchTerm: MutableLiveData<String> = MutableLiveData<String>()
    val searchTerm: String get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if (searchTerm.isBlank()) {
                flickrFetcher.fetchPhotos()
            } else {
                flickrFetcher.searchPhotos(searchTerm)
            }
        }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
    }

    //отмена текущего запроса (если такой есть) при завершении приложения
    override fun onCleared() {
        super.onCleared()
        flickrFetcher.cancelRequestsInFlight()
    }
}