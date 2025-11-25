package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class FavoritoCriado(
    val id: Int,
    @SerializedName("usuario") val id_usuario: Int? = null,
    @SerializedName("ong") val id_ong: Int? = null,
    @SerializedName("empresa") val id_empresa: Int
)
