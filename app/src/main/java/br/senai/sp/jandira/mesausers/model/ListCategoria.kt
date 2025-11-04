package br.senai.sp.jandira.mesausers.model

data class ListCategoria(
    var status: Boolean = false,
    var statusCode: Int = 0,
    var message: String = "",
    var categorias: List<Categoria>
)
