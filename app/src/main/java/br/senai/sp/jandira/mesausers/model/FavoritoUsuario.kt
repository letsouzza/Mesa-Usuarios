package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class FavoritoUsuario(
    @SerializedName("id_favorito") val idFavorito: Int,
    @SerializedName("id_usuario") val idUser: Int?,
    @SerializedName("id_ong") val idOng: Int?,
    @SerializedName("id_empresa") val idEmpresa: Int,
    @SerializedName("nome") val nomeEmpresa: String,
    val email: String,
    @SerializedName("cnpj_mei") val cnpj: String?,
    val telefone: String,
    @SerializedName("foto") val imagemEmpresa: String?
)
