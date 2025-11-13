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
import br.senai.sp.jandira.mesausers.screens.components.CardPedido
import br.senai.sp.jandira.mesausers.screens.components.Pedido
import br.senai.sp.jandira.mesausers.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    controleNavegacao: NavHostController?
) {
    // Lista de pedidos falsos (posteriormente virão da API)
    var listaPedidos by remember {
        mutableStateOf(
            listOf(
                Pedido(
                    id = "1",
                    nomeProduto = "Macarrão",
                    instituicao = "Assai Barueri",
                    validade = "13/10/2025",
                    quantidade = 5,
                    distancia = "5KM",
                    imagemProduto = "https://via.placeholder.com/60x60/8B4513/FFFFFF?text=Macarrão"
                ),
                Pedido(
                    id = "2",
                    nomeProduto = "Arroz",
                    instituicao = "Assai Barueri",
                    validade = "13/10/2025",
                    quantidade = 5,
                    distancia = "5KM",
                    imagemProduto = "https://via.placeholder.com/60x60/FFFFFF/8B4513?text=Arroz"
                ),
                Pedido(
                    id = "3",
                    nomeProduto = "Feijão",
                    instituicao = "Assai Barueri",
                    validade = "13/10/2025",
                    quantidade = 5,
                    distancia = "5KM",
                    imagemProduto = "https://via.placeholder.com/60x60/654321/FFFFFF?text=Feijão"
                ),
                Pedido(
                    id = "4",
                    nomeProduto = "Açúcar",
                    instituicao = "Assai Barueri",
                    validade = "10/10/2025",
                    quantidade = 5,
                    distancia = "5KM",
                    imagemProduto = "https://via.placeholder.com/60x60/F5F5F5/000000?text=Açúcar"
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
                text = stringResource(R.string.seus_pedidos),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = poppinsFamily,
                color = primaryLight,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Lista de pedidos
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaPedidos) { pedido ->
                    CardPedido(
                        pedido = pedido,
                        onDeleteClick = { pedidoId ->
                            // Remove o pedido da lista
                            listaPedidos = listaPedidos.filter { it.id != pedidoId }
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
private fun PedidosScreenPreview() {
    MesaTheme {
        PedidosScreen(null)
    }
}
