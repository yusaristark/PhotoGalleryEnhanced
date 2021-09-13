package com.github.yusaristark.photogalleryenhanced

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.yusaristark.photogalleryenhanced.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
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
            .baseUrl("https://www.flickr.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    /*
    Функция fetchContents() ставит в очередь сетевой запрос и обертывает результат в LiveData.
     */
    fun fetchContents(): LiveData<String> {
        val responseLiveData: MutableLiveData<String> = MutableLiveData()
        val flickrRequest: Call<String> = flickrApi.fetchContents()
        flickrRequest.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG, "Response received")
                responseLiveData.value = response.body()
            }
        })
        return responseLiveData
    }
}