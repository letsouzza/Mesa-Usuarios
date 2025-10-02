package br.senai.sp.jandira.mesausers.model

data class ResponseGeral(
    var status: Boolean = false,
    var statusCode: Int = 0,
    var message: String = ""
)
