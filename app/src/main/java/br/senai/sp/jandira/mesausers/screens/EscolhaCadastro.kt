package br.senai.sp.jandira.mesausers.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.screens.components.TipoCadastro
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily

@Composable
fun EscolhaCadastro(modifier: Modifier = Modifier) {
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
                            .size(180.dp)
                    )
                    Text(
                        text = stringResource(R.string.cadastrar),
                        fontSize = 35.sp,
                        fontFamily = poppinsFamily,
                        fontWeight =  FontWeight.Normal,
                        color = Color(0xFF1B4227)
                    )
                    Spacer(Modifier.padding(10.dp))
                    TipoCadastro(
                        image = painterResource(R.drawable.pessoa),
                        text = stringResource(R.string.pessoa)
                    )
                    Spacer(Modifier.padding(10.dp))
                    TipoCadastro(
                        image = painterResource(R.drawable.ongs),
                        text = stringResource(R.string.ongs)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EscolhaCadastroPreview() {
    EscolhaCadastro()
}