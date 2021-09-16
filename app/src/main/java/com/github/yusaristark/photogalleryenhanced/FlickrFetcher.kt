package com.github.yusaristark.photogalleryenhanced

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.yusaristark.photogalleryenhanced.api.FlickrApi
import com.github.yusaristark.photogalleryenhanced.api.FlickrResponse
import com.github.yusaristark.photogalleryenhanced.api.PhotoInterceptor
import com.github.yusaristark.photogalleryenhanced.api.PhotoResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

private const val TAG = "FlickrFetcher"

/*
FlickrFetcher — это своего рода простой репозиторий. Класс репозитория инкапсулирует логику доступа к данным из одного источника
или набора источников. Он определяет, как получать и хранить определенный набор данных, будь то в локальной базе данных или на удаленном сервере. Ваш код пользовательского
интерфейса будет запрашивать все данные из репозитория, потому что ему неважно, как данные хранятся или извлекаются на самом деле. Это детали реализации самого репозитория.
 */

class FlickrFetcher {
    private val flickrApi: FlickrApi
    private val currentCallsHashMap: ConcurrentHashMap<Call<FlickrResponse>, String> = ConcurrentHashMap()
    private lateinit var call: Call<FlickrResponse>

    init {
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(PhotoInterceptor()).build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    /*
    Функция fetchPhotos() ставит в очередь сетевой запрос и обертывает результат в LiveData.
     */
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.fetchPhotos())
    }

    fun searchPhotos(query: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.searchPhotos(query))
    }

    private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>) : LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        currentCallsHashMap[flickrRequest] = ""
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
                currentCallsHashMap.remove(flickrRequest)
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG, "Response received")
                currentCallsHashMap.remove(flickrRequest)
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                val galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems.filterNot { it.url.isBlank() }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }

    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap=$bitmap from Response=$response")
        return bitmap
    }

    //отмена текущего запроса
    fun cancelRequestsInFlight() {
        for (entry in currentCallsHashMap) {
            Log.i(TAG, "$entry")
            call = entry.key
            if (::call.isInitialized) {
                call.cancel()
            }
        }
    }
}