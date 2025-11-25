package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class ListFavoritosResponse(
    val status: Boolean,
    @SerializedName("status_code") var statusCode: Int = 0,
    val message: String,
    @SerializedName("result") val favoritos: List<FavoritoUsuario>?
)
