package br.senai.sp.jandira.mesausers.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.mesausers.R

@Composable
fun SplashScreen(navegacao: NavHostController?) {

    var visible by remember { mutableStateOf(false) }

    // Quando a tela abrir, esse efeito será chamado automaticamente
    LaunchedEffect(Unit) {
        visible = true // ativa o fade
    }

    // Animação que faz a opacidade (alpha) ir de 0f → 1f
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f, // começa invisível (0f), vai até visível (1f)
        animationSpec = tween(2000), // duração de 2 segundos
        label = "" // rótulo vazio, apenas para não dar warning
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B4227))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.logoclara),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(400.dp)
                    .alpha(alpha)

            )
        }
        IconButton(
            onClick = {navegacao!!.navigate("login")},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowCircleRight,
                contentDescription = "",
                tint = Color(0xFFFFE6B1),
                modifier = Modifier
                    .size(160.dp)
            )
        }
    }
}
//uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true
@Preview()
@Composable
private fun SplashScreenPreview() {
    SplashScreen(null)
//    Mobile_tccTheme(darkTheme = true) {
//        SplashScreen()
//    }
}