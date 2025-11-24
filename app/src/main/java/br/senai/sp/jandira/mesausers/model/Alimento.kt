package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class Alimento(
    var id: Int = 0,
    var nome: String = "",
    var quantidade: String = "",
    @SerializedName("data_de_validade") var prazo: String = "",
    var descricao: String = "",
    var peso: Double = 0.0,
    @SerializedName("id_tipo_peso") var idTipoPeso: Int = 1,
    var imagem: String = "",
    @SerializedName("id_empresa") var idEmpresa: Int = 1,
    val categorias: List<Categoria>, // ou List<Int> dependendo do tipo
    val empresa: Empresa? = null,
    val tipoPeso: List<TipoPeso>? = null
)
