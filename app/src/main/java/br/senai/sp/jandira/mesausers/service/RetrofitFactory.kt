package br.senai.sp.jandira.mesausers.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {
    private val BASE_URL = "http://10.107.144.28:8080/v1/mesa-plus/"

    private val RETROFIT_FACTORY = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getUserService() : UserService{
        return RETROFIT_FACTORY
            .create(UserService::class.java)
    }

    fun getSenhaService() : SenhaService{
        return RETROFIT_FACTORY
            .create(SenhaService::class.java)
    }

}