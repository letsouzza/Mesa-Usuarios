package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class PedidoUsuario(
    @SerializedName("id_pedido") val idPedido: Int,
    @SerializedName("id_usuario") val idUser: Int?,
    @SerializedName("id_ong") val idOng: Int?,
    @SerializedName("id_alimento") val idAlimento: Int,
    @SerializedName("quantidade_pedido") val quantidadePedido: Int,
    @SerializedName("nome_alimento") val nomeAlimento: String,
    val quantidade: String,
    val peso: String,
    @SerializedName("id_tipo_peso") val idTipoPeso: String?,
    val tipo: String,
    @SerializedName("data_de_validade") val dataValidade: String?,
    @SerializedName("imagem_alimento") val imagemAlimento: String?,
    @SerializedName("id_empresa") val idEmpresa: String,
    @SerializedName("nome_empresa") val nomeEmpresa: String,
    @SerializedName("foto_empresa") val imagemEmpresa: String?
)
