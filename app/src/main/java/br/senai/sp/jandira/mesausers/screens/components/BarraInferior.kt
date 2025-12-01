package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BarraInferior(controleNavegacao: NavHostController?) {
    NavigationBar (
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = Color(0xFF1B4227)
    ){
        NavigationBarItem(
            onClick = {
                controleNavegacao!!.navigate("mapa")
            },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Mapa",
                    Modifier.size(40.dp),
                    tint = Color(0xFFB9CE5D)
                )
            }
        )
        NavigationBarItem(
            onClick = {
                controleNavegacao!!.navigate("home")
            },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    Modifier.size(40.dp),
                    tint = Color(0xFFB9CE5D)
                )
            }
        )
        NavigationBarItem(
            onClick = {
                controleNavegacao!!.navigate("favoritos")
            },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorito",
                    Modifier.size(40.dp),
                    tint = Color(0xFFB9CE5D)
                )
            }
        )
        NavigationBarItem(
            onClick = {
                controleNavegacao!!.navigate("pedidos")
            },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Fastfood,
                    contentDescription = "Novo",
                    Modifier.size(40.dp),
                    tint = Color(0xFFB9CE5D)
                )
            }
        )
        NavigationBarItem(
            onClick = {
                controleNavegacao!!.navigate("perfil")
            },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonPin,
                    contentDescription = "Person",
                    Modifier.size(40.dp),
                    tint = Color(0xFFB9CE5D)
                )
            }
        )
    }

}

@Preview
@Composable
private fun BarraInferiorPreview(){
    BarraInferior(null)
}