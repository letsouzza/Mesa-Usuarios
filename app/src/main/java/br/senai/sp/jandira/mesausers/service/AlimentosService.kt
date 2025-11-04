package br.senai.sp.jandira.mesausers.service

import br.senai.sp.jandira.mesausers.model.ListAlimento
import br.senai.sp.jandira.mesausers.model.ListCategoria
import retrofit2.http.GET

interface AlimentosService {
    @GET("categoria")
    fun listCategoria(): retrofit2.Call<ListCategoria>

    @GET("alimentos")
    fun listAlimento(): retrofit2.Call<ListAlimento>
}