package br.senai.sp.jandira.mesausers.service

import br.senai.sp.jandira.mesausers.model.ListAlimento
import br.senai.sp.jandira.mesausers.model.ListAlimentoFiltro
import br.senai.sp.jandira.mesausers.model.ListCategoria
import retrofit2.http.GET
import retrofit2.http.Path

interface AlimentosService {
    @GET("categoria")
    fun listCategoria(): retrofit2.Call<ListCategoria>

    @GET("filtroCat/{id}")
    fun filtroCategoria(@Path("id") id: Int): retrofit2.Call<ListAlimentoFiltro>

    @GET("alimentos")
    fun listAlimento(): retrofit2.Call<ListAlimento>
}