package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class Empresa(
    var id: Int = 0,
    var nome: String = "",
    var email: String = "",
    var senha: String = "",
    @SerializedName("cnpj_mei") var cnpjMei: String = "",
    var telefone: String = "",
    var foto: String = "",
    var endereco: String = ""
)
