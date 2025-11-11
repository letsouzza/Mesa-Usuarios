package br.senai.sp.jandira.mesausers.model

import com.google.gson.annotations.SerializedName

data class AlimentoFiltro(
    @SerializedName("id_alimento") var id: Int = 0,
    @SerializedName("nome_alimento") var nome: String = "",
    var quantidade: String = "",
    var peso: String = "",
    @SerializedName("id_tipo_peso") var idTipoPeso: Int = 0,
    @SerializedName("data_de_validade") var prazo: String = "",
    var descricao: String = "",
    var imagem: String = "",
    @SerializedName("id_empresa") var idEmpresa: Int = 0,
    @SerializedName("nome_empresa") var nomeEmpresa: String = "",
    @SerializedName("foto_empresa") var fotoEmpresa: String? = null,
    @SerializedName("nome_categoria") var nomeCategoria: String = ""
)
