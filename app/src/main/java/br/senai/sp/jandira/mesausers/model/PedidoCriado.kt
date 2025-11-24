package br.senai.sp.jandira.mesausers.model

data class PedidoCriado(
    val id: Int,
    val id_usuario: Int? = null,
    val id_ong: Int? = null,
    val id_alimento: Int,
    val quantidade: Int
)
