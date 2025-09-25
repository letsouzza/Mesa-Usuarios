package br.senai.sp.jandira.mesausers.screens

import android.text.Layout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
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
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.screens.components.TipoCadastro
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily

@Composable
fun EscolhaCadastro(navegacao: NavHostController?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B4227))
    ){
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 60.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(Color(0xFFFFF9EB))
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(
                        painter = painterResource(R.drawable.logoescura),
                        contentDescription = "",
                        modifier = Modifier
                            .size(230.dp)
                    )
                    Text(
                        text = stringResource(R.string.cadastrar),
                        fontSize = 35.sp,
                        fontFamily = poppinsFamily,
                        fontWeight =  FontWeight.Normal,
                        color = Color(0xFF1B4227)
                    )
                    Spacer(Modifier.padding(20.dp))
                    TipoCadastro(
                        image = painterResource(R.drawable.pessoa),
                        text = stringResource(R.string.pessoa),
                        click = {navegacao!!.navigate("cadastroUser")}
                    )
                    Spacer(Modifier.padding(15.dp))
                    TipoCadastro(
                        image = painterResource(R.drawable.ongs),
                        text = stringResource(R.string.ongs),
                        click = {navegacao!!.navigate("cadastroOngs")}
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 70.dp)
                    ){
                        IconButton(
                            onClick = {navegacao!!.navigate("login")},
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowCircleLeft,
                                contentDescription = "",
                                tint = Color(0xFF1B4227),
                                modifier = Modifier
                                    .size(150.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EscolhaCadastroPreview() {
    EscolhaCadastro(null)
}