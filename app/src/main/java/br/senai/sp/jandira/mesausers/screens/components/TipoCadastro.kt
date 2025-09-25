package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily

@Composable
fun TipoCadastro(
    image: Painter,
    text: String,
    click: () -> Unit
) {

    Button(
        onClick = click,
        modifier = Modifier
            .width(330.dp)
            .height(110.dp)
            .shadow(10.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFEFCD)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                image,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 18.dp)
                    .size(90.dp)
            )
            Text(
                text,
                fontSize = 32.sp,
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1B4227)
            )
        }
    }
}

@Preview
@Composable
private fun TipoCadastroPreview() {
    TipoCadastro(image = painterResource(R.drawable.pessoa), text = "Pessoa", click = {})
}