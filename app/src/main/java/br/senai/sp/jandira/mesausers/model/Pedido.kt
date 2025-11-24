package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class Pedido(
    @SerializedName("id_usuario") val id_usuario: Int? = null,
    @SerializedName("id_ong") val id_ong: Int? = null,
    val id_alimento: Int,
    val quantidade: Int
)
