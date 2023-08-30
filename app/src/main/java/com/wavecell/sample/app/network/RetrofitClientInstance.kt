package com.wavecell.sample.app.network

import com.wavecell.sample.app.constants.AuthConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClientInstance {
    fun getRetrofitInstance(): Retrofit {

           return Retrofit.Builder()
                .baseUrl(AuthConstants.TOKEN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}