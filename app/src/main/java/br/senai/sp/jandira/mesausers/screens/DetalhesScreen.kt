package br.senai.sp.jandira.mesausers.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    controleNavegacao: NavHostController?,
    produtoId: String? = null
) {
    // Variáveis que virão da API
    var nomeProduto by remember { mutableStateOf("Macarrão") }
    var imagemProduto by remember { mutableStateOf("") }
    var nomeEmpresa by remember { mutableStateOf("Supermercado Atacadão") }
    var distanciaEmpresa by remember { mutableStateOf("5km") }
    var dataValidade by remember { mutableStateOf("25/09/2025") }
    var descricaoProduto by remember { mutableStateOf("Macarrão da marca Dona Benta") }
    var quantidade by remember { mutableStateOf("20") }
    var peso by remember { mutableStateOf("400ml") }
    var categorias by remember { mutableStateOf(listOf("Salgado", "Massas")) }
    
    // Estados da UI
    var isFavorito by remember { mutableStateOf(false) }
    var quantidadeCarrinho by remember { mutableStateOf(1) }
    val quantidadeMaxima = quantidade.toIntOrNull() ?: 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(
                        onClick = { controleNavegacao?.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isFavorito = !isFavorito }
                    ) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorito) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BarraInferior(controleNavegacao)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagem do produto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = imagemProduto.ifEmpty { "https://via.placeholder.com/400x300/8B4513/FFFFFF?text=Macarrão" },
                    contentDescription = nomeProduto,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Conteúdo principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundLight,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(24.dp)
            ) {
                // Nome do produto
                Text(
                    text = nomeProduto,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Card da empresa
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = secondaryLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ícone da empresa (placeholder)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = primaryLight,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nomeEmpresa.first().toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = nomeEmpresa,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryLight
                            )
                            Text(
                                text = distanciaEmpresa,
                                color = primaryLight.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                        
                        IconButton(
                            onClick = { isFavorito = !isFavorito }
                        ) {
                            Icon(
                                imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favoritar empresa",
                                tint = if (isFavorito) Color.Red else primaryLight
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Seção Detalhes
                Text(
                    text = stringResource(R.string.detalhes),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Data de validade
                Row {
                    Text(
                        text = stringResource(R.string.data_validade),
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight
                    )
                    Text(
                        text = " $dataValidade",
                        color = primaryLight.copy(alpha = 0.8f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Descrição
                Row {
                    Text(
                        text = "${stringResource(R.string.descricao)} ",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight
                    )
                    Text(
                        text = descricaoProduto,
                        color = primaryLight.copy(alpha = 0.8f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Quantidade
                Row {
                    Text(
                        text = "${stringResource(R.string.quantidade)} ",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight
                    )
                    Text(
                        text = quantidade,
                        color = primaryLight.copy(alpha = 0.8f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Peso
                Row {
                    Text(
                        text = "${stringResource(R.string.peso)} ",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight
                    )
                    Text(
                        text = peso,
                        color = primaryLight.copy(alpha = 0.8f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Seção Categoria
                Text(
                    text = stringResource(R.string.categoria),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Tags de categorias
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEach { categoria ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = secondaryLight),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = categoria,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = primaryLight,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Controle de quantidade e botão adicionar ao carrinho
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão adicionar ao carrinho
                    Button(
                        onClick = { /* Ação de adicionar ao carrinho */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryLight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.adicionar_carrinho),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    // Controle de quantidade
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (quantidadeCarrinho > 1) quantidadeCarrinho--
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = primaryLight,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Diminuir quantidade",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = quantidadeCarrinho.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = primaryLight
                        )

                        IconButton(
                            onClick = { 
                                if (quantidadeCarrinho < quantidadeMaxima) quantidadeCarrinho++
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = primaryLight,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Aumentar quantidade",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun DetalhesScreenPreview() {
    MesaTheme {
        DetalhesScreen(null)
    }
}
