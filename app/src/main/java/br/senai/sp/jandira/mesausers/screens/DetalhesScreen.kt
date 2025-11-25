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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import br.senai.sp.jandira.mesausers.model.Alimento
import br.senai.sp.jandira.mesausers.model.AlimentoResponse
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import br.senai.sp.jandira.mesausers.model.Pedido
import br.senai.sp.jandira.mesausers.model.PedidoResponse
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    navController: NavHostController?,
    alimentoId: Int = 0,
    sharedViewModel: SharedViewModel
) {
    // Estados para os dados do alimento
    var alimento by remember { mutableStateOf<Alimento?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estados da UI
    var isFavorito by remember { mutableStateOf(false) }
    var quantidadeCarrinho by remember { mutableStateOf(1) }
    val quantidadeMaxima = alimento?.quantidade?.toIntOrNull() ?: 1
    var isAddingToCart by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessageDialog by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun criarPedido(idAlimento: Int, quantidade: Int) {
        val pedido = if (sharedViewModel.tipo_usuario.equals("ong", ignoreCase = true)) {
            Pedido(
                id_ong = sharedViewModel.id_ong,
                id_alimento = idAlimento,
                quantidade = quantidade
            )
        } else {
            Pedido(
                id_usuario = sharedViewModel.id_usuario,
                id_alimento = idAlimento,
                quantidade = quantidade
            )
        }

        val call = RetrofitFactory().getPedidoService().criarPedido(pedido)

        call.enqueue(object : Callback<PedidoResponse> {
            override fun onResponse(
                call: Call<PedidoResponse>,
                response: Response<PedidoResponse>
            ) {
                val pedidoResponse = response.body()
                if (response.isSuccessful && pedidoResponse != null && pedidoResponse.status) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Pedido acrescentado com sucesso!")
                    }
                } else {
                    scope.launch {
                        val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                        Log.e("HomeScreen", "Erro ao criar pedido: ${response.code()} - $errorBody")
                        snackbarHostState.showSnackbar("Erro ao criar pedido: ${pedidoResponse?.message ?: response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<PedidoResponse>, t: Throwable) {
                scope.launch {
                    Log.e("HomeScreen", "Falha na conexão ao criar pedido", t)
                    snackbarHostState.showSnackbar("Falha na conexão. Tente novamente mais tarde.")
                }
            }
        })
    }

    // Carregar detalhes do alimento quando o ID mudar
    LaunchedEffect(alimentoId) {
        if (alimentoId > 0) {
            val call = RetrofitFactory().getAlimentoService().getAlimentoPorId(alimentoId)
            call.enqueue(object : Callback<AlimentoResponse> {
                override fun onResponse(
                    call: Call<AlimentoResponse>,
                    response: Response<AlimentoResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        response.body()?.let { alimentoResponse ->
                            if (alimentoResponse.alimento.isNotEmpty()) {
                                alimento = alimentoResponse.alimento[0]
                            } else {
                                errorMessage = "Alimento não encontrado"
                            }
                        } ?: run {
                            errorMessage = "Resposta inválida do servidor"
                        }
                    } else {
                        errorMessage = "Erro ao carregar: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<AlimentoResponse>, t: Throwable) {
                    isLoading = false
                    errorMessage = "Falha na conexão: ${t.message}"
                    Log.e("DetalhesScreen", "Erro na requisição", t)
                }
            })
        } else {
            isLoading = false
            errorMessage = "ID do alimento não fornecido"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController?.popBackStack() }
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
            BarraInferior(navController)
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
                    .height(250.dp)
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryLight)
                        }
                    }

                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = Color.Red,
                                fontFamily = poppinsFamily
                            )
                        }
                    }

                    alimento != null -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(alimento?.imagem ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = alimento?.nome,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
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
                    text = alimento?.nome ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight,
                    fontFamily = poppinsFamily
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
                                text = alimento?.empresa?.nome?.firstOrNull()?.toString() ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = alimento?.empresa?.nome ?: "",
                                fontWeight = FontWeight.SemiBold,
                                color = primaryLight,
                                fontFamily = poppinsFamily
                            )
                            Text(
                                text = alimento?.empresa?.email ?: "",
                                color = primaryLight.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontFamily = poppinsFamily
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
                    text = "Detalhes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Data de validade
                Row {
                    Text(
                        text = "Data de validade: ",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight,
                        fontFamily = poppinsFamily
                    )
                    Text(
                        text = alimento?.prazo ?: "",
                        color = primaryLight.copy(alpha = 0.8f),
                        fontFamily = poppinsFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Descrição
                Row {
                    Text(
                        text = "Descrição: ",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight
                    )
                    Text(
                        text = alimento?.descricao ?: "",
                        color = primaryLight.copy(alpha = 0.8f),
                        fontFamily = poppinsFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantidade
                Row {
                    Text(
                        text = "Quantidade: ",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight
                    )
                    Text(
                        text = alimento?.quantidade ?: "0",
                        color = primaryLight.copy(alpha = 0.8f),
                        fontFamily = poppinsFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Peso
                Row {
                    Text(
                        text = "Peso: ",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryLight
                    )
                    Text(
                        text = "${alimento?.peso ?: ""} ${alimento?.tipoPeso?.firstOrNull()?.tipo ?: ""}",
                        color = primaryLight.copy(alpha = 0.8f),
                        fontFamily = poppinsFamily
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Seção Categoria
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryLight
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tags de categorias
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    alimento?.categorias?.forEach { categoria ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = secondaryLight),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = categoria.nome,
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
                    // Controle de quantidade
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (quantidadeCarrinho > 1) quantidadeCarrinho-- },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(primaryLight.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remover",
                                tint = primaryLight
                            )
                        }

                        Text(
                            text = quantidadeCarrinho.toString(),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontWeight = FontWeight.Bold,
                            color = primaryLight
                        )

                        IconButton(
                            onClick = { if (quantidadeCarrinho < quantidadeMaxima) quantidadeCarrinho++ },
                            enabled = quantidadeCarrinho < quantidadeMaxima,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(primaryLight.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar",
                                tint = if (quantidadeCarrinho < quantidadeMaxima) primaryLight else Color.Gray
                            )
                        }
                    }

                    // Botão adicionar ao carrinho
                    Button(
                        onClick = { criarPedido(
                            alimentoId,
                            quantidadeCarrinho
                        )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryLight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp),
                        enabled = !isAddingToCart
                    ) {
                        if (isAddingToCart) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Adicionar ao carrinho",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adicionar ao carrinho", color = Color.White)
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
//        DetalhesScreen(
//            navController = null,
//            alimentoId = 1,
//            sharedViewModel = sharedViewModel // ID de exemplo para preview
//        )
    }
}