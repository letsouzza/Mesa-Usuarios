package br.senai.sp.jandira.mesausers.model

data class PedidoResponse(
    var status: Boolean = false,
    var message: String = "",
    var pedido: PedidoCriado? = null
)
