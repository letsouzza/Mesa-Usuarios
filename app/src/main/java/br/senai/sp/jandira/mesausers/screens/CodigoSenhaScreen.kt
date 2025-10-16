package br.senai.sp.jandira.mesausers.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.CodigoRecuperacao
import br.senai.sp.jandira.mesausers.model.RecuperarSenha
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

@Composable
fun CodigoSenha(navegacao: NavHostController?) {

    var controleNavegacao = rememberNavController()
    var codigoState by remember {mutableStateOf("")}
    var mostrarMensagemSucesso by remember { mutableStateOf(false) }

    val senhaApi = RetrofitFactory().getSenhaService()

    val context = LocalContext.current
    val userFile = context.getSharedPreferences("user_file", Context.MODE_PRIVATE)
    val emailState = userFile.getString("email", "")
    val tipoState = userFile.getString("tipo", "")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B4227))
    ){
        Column(
            modifier= Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
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
            ){

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    Spacer(Modifier.padding(top = 20.dp))
                    Text(
                        text= stringResource(R.string.recuperacao_senha),
                        fontSize = 30.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1B4227),
                        modifier= Modifier
                            .padding(start = 20.dp)
                    )
                    Column {
                        BasicTextField(
                            value = codigoState,
                            onValueChange = { codigoState = it },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontFamily = poppinsFamily
                            ),
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
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (codigoState.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.codigo),
                                            fontSize = 20.sp,
                                            fontFamily = poppinsFamily,
                                            color = Color(0x99000000)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        Text(
                            text = stringResource(R.string.reenviar_codigo),
                            fontSize = 16.sp,
                            fontFamily = poppinsFamily,
                            fontWeight = FontWeight.Bold,
                            color= Color(0x99000000),
                            modifier = Modifier
                                .padding(start = 30.dp, top = 10.dp)
                                .clickable{
                                    val body = RecuperarSenha(
                                        email = "$emailState",
                                        tipo = "$tipoState"
                                    )

                                    GlobalScope.launch(Dispatchers.IO) {
                                        try {
                                            // 1. Chama a API no thread de IO
                                            val recuperar = senhaApi.sendEmail(body).await()

                                            // 2. Muda para o thread principal para exibir o Toast
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    context,
                                                    "Código Reenviado com sucesso!", // Mensagem melhorada
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            println("Código reenviado com sucesso")

                                        } catch (e: Exception) {
                                            // Lida com erros de rede ou da API
                                            println("Erro ao reenviar código: ${e.message}")
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    context,
                                                    "Falha ao reenviar código.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                }
                        )
                    }
                    Button(
                        onClick = {
                            val body = CodigoRecuperacao(
                                email = " $emailState",
                                tipo = " $tipoState",
                                codigo = codigoState,
                            )

                            GlobalScope.launch(Dispatchers.IO){
                                val codigo = senhaApi.sendCodigo(body).await()
                                mostrarMensagemSucesso = true
                                println("deu CERTOOOOOOOO")
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 30.dp)
                            .width(210.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF1B4227))
                    ) {
                        Text(
                            text = stringResource(R.string.validar_codigo),
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
                        text = "Aviso",
                        fontSize = 25.sp,
                        fontFamily = poppinsFamily,
                        fontWeight =  FontWeight.SemiBold,
                        color = Color(0xFF1B4227)

                    )
                },
                text = {
                    Text(
                        text = "Código verificado! Escolha sua nova senha",
                        fontSize = 15.sp,
                        fontFamily = poppinsFamily,
                        color = Color(0x99000000)
                    )
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = {
                            navegacao!!.navigate("atualizarSenha")
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
private fun CodigoSenhaPreview() {
    CodigoSenha(null)
}