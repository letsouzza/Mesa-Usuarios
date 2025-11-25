package br.senai.sp.jandira.mesausers.service

import br.senai.sp.jandira.mesausers.model.Favorito
import br.senai.sp.jandira.mesausers.model.FavoritoResponse
import br.senai.sp.jandira.mesausers.model.ListFavoritosResponse
import br.senai.sp.jandira.mesausers.model.ListPedidosResponse
import br.senai.sp.jandira.mesausers.model.ResponseGeral
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface FavoritoService {

    @Headers("Content-Type: application/json")
    @POST("favoritoUser")
    fun criarFavorito(@Body favorito: Favorito): Call<FavoritoResponse>

    @Headers("Content-Type: application/json")
    @GET("favorito")
    fun getFavoritos(@QueryMap params: Map<String, String>): Call<ListFavoritosResponse>

    @Headers("Content-Type: application/json")
    @DELETE("favorito/{id}")
    fun deletarFavorito(@Path("id") id: Int): Call<ResponseGeral>
}