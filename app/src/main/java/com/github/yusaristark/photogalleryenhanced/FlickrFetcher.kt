package com.github.yusaristark.photogalleryenhanced

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.yusaristark.photogalleryenhanced.api.FlickrApi
import com.github.yusaristark.photogalleryenhanced.api.FlickrResponse
import com.github.yusaristark.photogalleryenhanced.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetcher"

/*
FlickrFetcher — это своего рода простой репозиторий. Класс репозитория инкапсулирует логику доступа к данным из одного источника
или набора источников. Он определяет, как получать и хранить определенный набор данных, будь то в локальной базе данных или на удаленном сервере. Ваш код пользовательского
интерфейса будет запрашивать все данные из репозитория, потому что ему неважно, как данные хранятся или извлекаются на самом деле. Это детали реализации самого репозитория.
 */

class FlickrFetcher {
    private val flickrApi: FlickrApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    /*
    Функция fetchContents() ставит в очередь сетевой запрос и обертывает результат в LiveData.
     */
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val flickrRequest: Call<FlickrResponse> = flickrApi.fetchPhotos()
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received")
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                val galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems.filterNot { it.url.isBlank() }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }
}