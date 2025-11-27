package br.senai.sp.jandira.mesausers.model

data class OngResponse(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val cnpj: String = "",
    val telefone: String = "",
    val foto: String = "",
    val endereco: String = "",
    val descricao: String = ""
)
