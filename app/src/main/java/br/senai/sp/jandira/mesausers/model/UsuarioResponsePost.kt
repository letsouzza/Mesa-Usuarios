package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class UsuarioResponsePost(
    var id: Int = 0,
    var nome: String = "",
    var email: String = "",
    var senha: String = "",
    var cpf: String = "",
    @SerializedName("cnpj_mei") var cnpjMei: String = "",
    var telefone: String = "",
    @SerializedName("foto_perfil") var foto: String = ""
)
