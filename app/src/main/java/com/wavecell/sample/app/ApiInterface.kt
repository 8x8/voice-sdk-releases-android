package com.wavecell.sample.app

import com.wavecell.sample.app.models.AuthRequestBody
import com.wavecell.sample.app.models.AuthResponse
import retrofit2.http.*

interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST
    suspend fun getAuthToken(@Url url: String, @Body authRequestBody: AuthRequestBody): AuthResponse
}
