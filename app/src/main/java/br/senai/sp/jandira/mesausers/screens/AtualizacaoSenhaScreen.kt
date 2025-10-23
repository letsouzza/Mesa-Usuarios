package br.senai.sp.jandira.mesausers.screens

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.EsqueciSenha
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

@Composable
fun AtualizacaoSenha(navegacao: NavHostController?) {

    var controleNavegacao = rememberNavController()
    var novaSenhaState by remember { mutableStateOf("") }
    var confirmarSenhaState by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var novaSenhaVisivel by remember { mutableStateOf(false) }
    var mostrarMensagemSucesso by remember { mutableStateOf(false) }

    val senhaApi = RetrofitFactory().getSenhaService()

    val context = LocalContext.current
    val userFile = context.getSharedPreferences("user_file", Context.MODE_PRIVATE)
    val emailState = userFile.getString("email", "")
    val tipoState = userFile.getString("tipo", "")

    var senhasDiferentes by remember { mutableStateOf(false) }

    fun validarSenhas(): Boolean {
        senhasDiferentes = novaSenhaState.isNotEmpty() &&
                confirmarSenhaState.isNotEmpty() &&
                novaSenhaState != confirmarSenhaState
        return novaSenhaState.isNotEmpty() &&
                confirmarSenhaState.isNotEmpty() &&
                novaSenhaState == confirmarSenhaState
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B4227))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logoclara),
                contentDescription = "",
                modifier = Modifier
                    .padding(10.dp)
                    .size(370.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(580.dp),
                colors = CardDefaults.cardColors(Color(0xFFFFF9EB)),
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(Modifier.padding(top = 20.dp))
                    Text(
                        text = stringResource(R.string.atualizacao_senha),
                        fontSize = 30.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1B4227),
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )
                    // 🔹 Campo: Nova Senha
                    BasicTextField(
                        value = novaSenhaState,
                        onValueChange = {
                            novaSenhaState = it
                            validarSenhas()
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = poppinsFamily
                        ),
                        visualTransformation = if (novaSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .border(
                                width = 3.dp,
                                color = Color(0xFFFFE6B1),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(30.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (novaSenhaState.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.nova_senha),
                                            fontSize = 20.sp,
                                            fontFamily = poppinsFamily,
                                            color = Color(0x99000000)
                                        )
                                    }
                                    innerTextField()
                                }

                                //  Mostrar/Esconder senha
                                IconButton(onClick = { novaSenhaVisivel = !novaSenhaVisivel }) {
                                    val icon =
                                        if (novaSenhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Mostrar/Esconder nova senha",
                                        tint = Color(0xFF1B4227)
                                    )
                                }

                                //  Ícone de alerta se senhas não coincidem
                                if (senhasDiferentes) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Senhas diferentes",
                                        tint = Color.Red,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )

                    //  Campo: Confirmar Senha
                    BasicTextField(
                        value = confirmarSenhaState,
                        onValueChange = {
                            confirmarSenhaState = it
                            validarSenhas()
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = poppinsFamily
                        ),
                        visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .border(
                                width = 3.dp,
                                color = Color(0xFFFFE6B1),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(30.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (confirmarSenhaState.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.confirmar_senha),
                                            fontSize = 20.sp,
                                            fontFamily = poppinsFamily,
                                            color = Color(0x99000000)
                                        )
                                    }
                                    innerTextField()
                                }

                                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                    val icon =
                                        if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Mostrar/Esconder confirmar senha",
                                        tint = Color(0xFF1B4227)
                                    )
                                }

                                //  Ícone de alerta também aqui
                                if (senhasDiferentes) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Senhas diferentes",
                                        tint = Color.Red,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )

                    //  Mensagem de erro abaixo do segundo campo
                    if (senhasDiferentes) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Erro",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "As senhas digitadas não são compatíveis",
                                color = Color.Red,
                                fontSize = 16.sp,
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (validarSenhas()) {
                                isSenhaError = false

                                val body = EsqueciSenha(
                                    email = "$emailState",
                                    tipo = "$tipoState",
                                    senha = novaSenhaState,
                                )

                                GlobalScope.launch(Dispatchers.IO) {
                                    try {
                                        // 1. Chamar a API e AGUARDAR o resultado
                                        val response = senhaApi.atualizarSenha(body).await()

                                        println(response.message)
                                        // 2. Mudar para o thread principal para atualizar o estado da UI
                                        if (response.message == "Item atualizado com sucesso!!") {
                                            // Certifique-se de usar `withContext(Dispatchers.Main)` ou `launch(Dispatchers.Main)` para atualizar o estado da UI
                                            withContext(Dispatchers.Main) {
                                                mostrarMensagemSucesso = true
                                                // console log para debug
                                                println("Senha atualizada com SUCESSO! Mensagem: ${response.message}")
                                            }
                                        } else {
                                            // Tratar falha da API (ex: senha muito curta, usuário não encontrado)
                                            withContext(Dispatchers.Main) {
                                                // Aqui você pode adicionar um estado de erro (e.g., isAPIError)
                                                println("Falha na atualização de senha. Status: ${response.statusCode}")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        // Tratar erros de rede ou exceções gerais
                                        withContext(Dispatchers.Main) {
                                            println("Erro ao conectar ou processar: ${e.message}")
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 30.dp)
                            .width(210.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF1B4227))
                    ) {
                        Text(
                            text = stringResource(R.string.atualizar),
                            fontSize = 20.sp,
                            fontFamily = poppinsFamily,
                            color = Color.White
                        )
                    }
                    BarraInferior(controleNavegacao)
                }
            }
        }
        if (mostrarMensagemSucesso){
            AlertDialog(
                onDismissRequest = {
                    mostrarMensagemSucesso = false
                },
                title = {
                    Text(
                        text = "Sucesso",
                        fontSize = 25.sp,
                        fontFamily = poppinsFamily,
                        fontWeight =  FontWeight.SemiBold,
                        color = Color(0xFF1B4227)

                    )
                },
                text = {
                    Text(
                        text = "Senha atualizada com sucesso!",
                        fontSize = 15.sp,
                        fontFamily = poppinsFamily,
                        color = Color(0x99000000)
                    )
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = {
                            navegacao!!.navigate("login")
                        }
                    ){
                        Text(
                            text= "Ok",
                            fontSize = 18.sp,
                            fontFamily = poppinsFamily,
                            fontWeight =  FontWeight.SemiBold,
                            color = Color(0xFF1B4227)
                        )
                    }
                }
            )
        }
    }
}


@Preview
@Composable
private fun AtualizacaoSenhaPreview() {
    AtualizacaoSenha(null)
}