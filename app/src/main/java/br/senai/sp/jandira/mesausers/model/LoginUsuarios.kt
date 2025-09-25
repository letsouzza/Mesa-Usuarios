package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class LoginUsuarios(
    var email: String = "",
    var senha: String = "",
    var tipo: String = ""
)
