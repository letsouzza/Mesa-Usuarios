package br.senai.sp.jandira.mesausers.model

data class ResponseCadastro(
    var status: Boolean = false,
    var statusCode: Int = 0,
    var message: String = "",
    var usuario: UsuarioResponsePost
)
