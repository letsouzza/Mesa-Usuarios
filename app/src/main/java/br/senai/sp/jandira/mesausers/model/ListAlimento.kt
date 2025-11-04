package br.senai.sp.jandira.mesausers.model

data class ListAlimento(
    var status: Boolean = false,
    var statusCode: Int = 0,
    var message: String = "",
    var alimentos: List<Alimento>
)
