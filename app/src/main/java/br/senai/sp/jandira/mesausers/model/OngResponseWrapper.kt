package br.senai.sp.jandira.mesausers.model

data class OngResponseWrapper(
    val status: Boolean = false,
    val status_code: Int = 0,
    val message: String = "",
    val ong: OngResponse
)
