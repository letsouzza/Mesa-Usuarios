package br.senai.sp.jandira.mesausers.service

import br.senai.sp.jandira.mesausers.model.ListEmpresa
import br.senai.sp.jandira.mesausers.model.LoginUsuarios
import br.senai.sp.jandira.mesausers.model.OngsCadastro
import br.senai.sp.jandira.mesausers.model.ResponseCadastro
import br.senai.sp.jandira.mesausers.model.UserCadastro
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {

    @Headers("Content-Type: application/json")
    @POST("usuario")
    fun insertUser(@Body user: UserCadastro): Call<ResponseCadastro>

    @Headers("Content-Type: application/json")
    @POST("ong")
    fun insertOngs(@Body user: OngsCadastro): Call<ResponseCadastro>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun login(@Body login: LoginUsuarios): Call<ResponseCadastro>

    @GET("empresa")
    fun listEmpresa(): retrofit2.Call<ListEmpresa>
}