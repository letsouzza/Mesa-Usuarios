package br.senai.sp.jandira.mesausers.model

data class AlimentoResponse(
    val status: Boolean,
    val status_code: Int,
    val alimento: List<Alimento>
)
