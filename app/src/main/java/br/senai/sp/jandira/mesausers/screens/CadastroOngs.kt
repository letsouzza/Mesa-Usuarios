package br.senai.sp.jandira.mesausers.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.await

@Composable
fun CadastroOngs(modifier: Modifier = Modifier) {

    var nameState by remember {mutableStateOf("")}
    var emailState by remember {mutableStateOf("")}
    var telefoneState by remember {mutableStateOf("")}
    var senhaState by remember {mutableStateOf("")}
    var senhaVisivel by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B4227))
    ){
        Column(
            modifier.fillMaxSize()
        ){
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xD9FFF9EB)
                )
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(R.drawable.logoescura),
                        contentDescription = "",
                        modifier = Modifier
                            .size(180.dp)
                    )
                    Text(
                        text = stringResource(R.string.cadastre_se),
                        fontSize = 35.sp,
                        fontFamily = poppinsFamily,
                        fontWeight =  FontWeight.Normal,
                        color = Color(0xFF1B4227)
                    )
                    Spacer(Modifier.padding(7.dp))
                    OutlinedTextField(
                        value = nameState,
                        onValueChange = { it ->
                            nameState = it
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFFFFF),
                            focusedContainerColor = Color(0xFFFFFFFF),
                            unfocusedBorderColor = Color(0xFF1B4227),
                            focusedBorderColor = Color(0xFF1B4227),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.nome
                                ),
                                fontSize = 20.sp,
                                fontFamily = poppinsFamily,
                                color = Color(0x99000000)
                            )
                        },
                        modifier = Modifier
                            .width(315.dp)

                    )
                    Spacer(Modifier.padding(5.dp))
                    OutlinedTextField(
                        value = emailState,
                        onValueChange = { it ->
                            emailState = it
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFFFFF),
                            focusedContainerColor = Color(0xFFFFFFFF),
                            unfocusedBorderColor = Color(0xFF1B4227),
                            focusedBorderColor = Color(0xFF1B4227),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.email
                                ),
                                fontSize = 20.sp,
                                fontFamily = poppinsFamily,
                                color = Color(0x99000000)
                            )
                        },
                        modifier = Modifier
                            .width(315.dp)

                    )
                    Spacer(Modifier.padding(5.dp))
                    OutlinedTextField(
                        value = telefoneState,
                        onValueChange = { it ->
                            telefoneState = it
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFFFFF),
                            focusedContainerColor = Color(0xFFFFFFFF),
                            unfocusedBorderColor = Color(0xFF1B4227),
                            focusedBorderColor = Color(0xFF1B4227),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.telefone
                                ),
                                fontSize = 20.sp,
                                fontFamily = poppinsFamily,
                                color = Color(0x99000000)
                            )
                        },
                        modifier = Modifier
                            .width(315.dp)

                    )
                    Spacer(Modifier.padding(5.dp))
                    OutlinedTextField(
                        value = senhaState,
                        onValueChange = { it ->
                            senhaState = it
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFFFFFFF),
                            focusedContainerColor = Color(0xFFFFFFFF),
                            unfocusedBorderColor = Color(0xFF1B4227),
                            focusedBorderColor = Color(0xFF1B4227),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            val icon = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff

                            IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "",
                                    tint = Color(0xFF1B4227)
                                )
                            }
                        },
                        visualTransformation =
                            if (senhaVisivel) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.senha
                                ),
                                fontSize = 20.sp,
                                fontFamily = poppinsFamily,
                                color = Color(0x99000000)
                            )
                        },
                        modifier = Modifier
                            .width(315.dp)
                    )
                    Spacer(Modifier.padding(30.dp))
                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .width(230.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFDA8B)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.cadastrar),
                            fontSize = 20.sp,
                            fontFamily = poppinsFamily,
                            fontWeight =  FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CadastroOngsPreview() {
    CadastroOngs()
}