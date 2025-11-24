package br.senai.sp.jandira.mesausers.service

import br.senai.sp.jandira.mesausers.model.Pedido
import br.senai.sp.jandira.mesausers.model.PedidoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PedidoService {
    @Headers("Content-Type: application/json")
    @POST("pedidoUsuario")
    fun criarPedido(@Body pedido: Pedido): Call<PedidoResponse>
}