package br.senai.sp.jandira.mesaparceiros.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily

@Composable
fun RecuperacaoSenha(navegacao: NavHostController?) {

    var controleNavegacao = rememberNavController()
    var emailState by remember {mutableStateOf("")}

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
                    BasicTextField(
                        value = emailState,
                        onValueChange = { emailState = it },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = poppinsFamily
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp) // 👈 padding lateral
                            .border(
                                width = 3.dp, // 👈 borda grossa
                                color = Color(0xFFFFE6B1),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(30.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp), // 👈 espaço interno
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (emailState.isEmpty()) {
                                    Text(
                                        text = "Email", // 👈 label dentro do campo
                                        fontSize = 20.sp,
                                        fontFamily = poppinsFamily,
                                        color = Color(0x99000000)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 30.dp)
                            .width(180.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF1B4227))
                    ) {
                        Text(
                            text = stringResource(R.string.recuperar),
                            fontSize = 20.sp,
                            fontFamily = poppinsFamily,
                            color = Color.White

                        )
                    }
                    BarraInferior(controleNavegacao)
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecuperacaoSenhaPreview() {
    RecuperacaoSenha(null)
}