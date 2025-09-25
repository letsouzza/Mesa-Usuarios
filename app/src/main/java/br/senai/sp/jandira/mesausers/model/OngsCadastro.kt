package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class OngsCadastro(
    var id: Int = 0,
    var nome: String = "",
    var email: String = "",
    var senha: String = "",
    var telefone: String = "",
    var foto: String = ""
)
