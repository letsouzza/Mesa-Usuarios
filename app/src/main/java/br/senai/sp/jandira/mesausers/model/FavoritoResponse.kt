package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class FavoritoResponse(
    var status: Boolean = false,
    @SerializedName("status_code") var statusCode: Int = 0,
    var message: String = "",
    var favorito: FavoritoCriado? = null
)
