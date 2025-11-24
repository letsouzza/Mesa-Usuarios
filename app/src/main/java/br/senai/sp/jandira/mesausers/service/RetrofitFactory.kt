package br.senai.sp.jandira.mesausers.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {
    private val BASE_URL = "https://mesaplus-bbh2hhheaab7f6ep.canadacentral-01.azurewebsites.net/v1/mesa-plus/"

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

    fun getAlimentoService() : AlimentosService{
        return RETROFIT_FACTORY
            .create(AlimentosService::class.java)
    }

    fun getPedidoService() : PedidoService{
        return RETROFIT_FACTORY
            .create(PedidoService::class.java)
    }

}