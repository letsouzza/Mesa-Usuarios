package br.senai.sp.jandira.mesausers.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.CardInstituicaoFavorita
import br.senai.sp.jandira.mesausers.screens.components.InstituicaoFavorita
import br.senai.sp.jandira.mesausers.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    controleNavegacao: NavHostController?
) {
    // Lista de instituições favoritas (posteriormente virão da API)
    var listaFavoritos by remember {
        mutableStateOf(
            listOf(
                InstituicaoFavorita(
                    id = "1",
                    nome = "Assaí",
                    distancia = "5KM",
                    logoUrl = "https://via.placeholder.com/60x60/FF6B00/FFFFFF?text=Assaí",
                    isFavorito = true
                ),
                InstituicaoFavorita(
                    id = "2",
                    nome = "Atacadão",
                    distancia = "5KM",
                    logoUrl = "https://via.placeholder.com/60x60/E31E24/FFFFFF?text=Atacadão",
                    isFavorito = true
                ),
                InstituicaoFavorita(
                    id = "3",
                    nome = "Madero",
                    distancia = "5KM",
                    logoUrl = "https://via.placeholder.com/60x60/8B4513/FFFFFF?text=Madero",
                    isFavorito = true
                ),
                InstituicaoFavorita(
                    id = "4",
                    nome = "Mc Donald's",
                    distancia = "5KM",
                    logoUrl = "https://via.placeholder.com/60x60/FFC72C/FF0000?text=McD",
                    isFavorito = true
                ),
                InstituicaoFavorita(
                    id = "5",
                    nome = "Mercado Barbosa",
                    distancia = "5KM",
                    logoUrl = "https://via.placeholder.com/60x60/0066CC/FFFFFF?text=MB",
                    isFavorito = true
                )
            )
        )
    }

    Scaffold(
        topBar = {
            BarraDeTitulo()
        },
        bottomBar = {
            BarraInferior(controleNavegacao)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundLight)
                .padding(24.dp)
        ) {
            // Título da tela
            Text(
                text = stringResource(R.string.instituicoes_favoritas),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppinsFamily,
                color = primaryLight,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Lista de favoritos
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaFavoritos) { instituicao ->
                    CardInstituicaoFavorita(
                        instituicao = instituicao,
                        onFavoritoClick = { instituicaoId ->
                            // Lógica para adicionar aos favoritos (se necessário)
                            // Aqui você pode fazer uma chamada para a API
                        },
                        onRemoverFavorito = { instituicaoId ->
                            // Remove a instituição da lista de favoritos
                            listaFavoritos = listaFavoritos.filter { it.id != instituicaoId }
                            // Aqui você pode fazer uma chamada para a API para remover dos favoritos
                        }
                    )
                }
                
                // Espaçamento extra no final da lista
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun FavoritosScreenPreview() {
    MesaTheme {
        FavoritosScreen(null)
    }
}
