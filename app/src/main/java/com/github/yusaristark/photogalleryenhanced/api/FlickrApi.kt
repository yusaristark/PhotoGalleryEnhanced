package com.github.yusaristark.photogalleryenhanced.api

import retrofit2.Call
import retrofit2.http.GET

private const val API_KEY = "a8a1d06391cfbd6183ad22e6984a9df9"

interface FlickrApi {

    @GET("services/rest/?" +
            "method=flickr.interestingness.getList" +
            "&api_key=$API_KEY" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>
}