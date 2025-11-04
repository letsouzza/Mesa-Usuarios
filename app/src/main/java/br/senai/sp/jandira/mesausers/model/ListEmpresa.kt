package br.senai.sp.jandira.mesausers.model

data class ListEmpresa(
    var status: Boolean = false,
    var statusCode: Int = 0,
    var message: String = "",
    var empresas: List<Empresa>
)
