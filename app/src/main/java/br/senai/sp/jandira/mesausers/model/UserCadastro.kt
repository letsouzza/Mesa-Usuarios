package br.senai.sp.jandira.mesausers.model

data class UserCadastro(
    var id: Int = 0,
    var nome: String = "",
    var email: String = "",
    var senha: String = "",
    var cpf: String = "",
    var telefone: String = "",
    var foto: String = ""
)
