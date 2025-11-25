package br.senai.sp.jandira.mesausers.service

import br.senai.sp.jandira.mesausers.model.ListPedidosResponse
import br.senai.sp.jandira.mesausers.model.Pedido
import br.senai.sp.jandira.mesausers.model.PedidoResponse
import br.senai.sp.jandira.mesausers.model.ResponseGeral
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PedidoService {
    @Headers("Content-Type: application/json")
    @POST("pedidoUsuario")
    fun criarPedido(@Body pedido: Pedido): Call<PedidoResponse>

    @Headers("Content-Type: application/json")
    @GET("pedido")
    fun getPedidos(@QueryMap params: Map<String, String>): Call<ListPedidosResponse>

    @Headers("Content-Type: application/json")
    @DELETE("pedido/{id}")
    fun deletarPedido(@Path("id") id: Int): Call<ResponseGeral>
}