package br.senai.sp.jandira.mesausers.service

import br.senai.sp.jandira.mesausers.model.CodigoRecuperacao
import br.senai.sp.jandira.mesausers.model.EsqueciSenha
import br.senai.sp.jandira.mesausers.model.RecuperarSenha
import br.senai.sp.jandira.mesausers.model.ResponseCadastro
import br.senai.sp.jandira.mesausers.model.ResponseGeral
import br.senai.sp.jandira.mesausers.model.UsuarioResponsePost
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface SenhaService {

    @Headers("Content-Type: application/json")
    @POST("enviar-codigo")
    fun sendEmail(@Body email: RecuperarSenha): Call<ResponseCadastro>

    @Headers("Content-Type: application/json")
    @POST("codigo-recuperacao")
    fun sendCodigo(@Body codigo: CodigoRecuperacao): Call<ResponseGeral>

    @Headers("Content-Type: application/json")
    @PUT("nova-senha")
    fun atualizarSenha(@Body senha: EsqueciSenha): Call<ResponseGeral>
}