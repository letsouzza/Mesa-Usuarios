package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class ListAlimentoFiltro(
    var status: Boolean = false,
    @SerializedName("status_code") var statusCode: Int = 0,
    @SerializedName("resultFiltro") var resultFiltro: List<AlimentoFiltro>
)
