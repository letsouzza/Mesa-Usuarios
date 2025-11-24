package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class ListPedidosResponse(
    val status: Boolean,
    val message: String,
    @SerializedName("result") val pedidos: List<PedidoUsuario>?
)
