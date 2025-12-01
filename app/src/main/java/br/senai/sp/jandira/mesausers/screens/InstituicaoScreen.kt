package br.senai.sp.jandira.mesausers.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.senai.sp.jandira.mesausers.model.AlimentoFiltro
import br.senai.sp.jandira.mesausers.model.Empresa
import br.senai.sp.jandira.mesausers.model.Favorito
import br.senai.sp.jandira.mesausers.model.FavoritoResponse
import br.senai.sp.jandira.mesausers.model.ListAlimentoFiltro
import br.senai.sp.jandira.mesausers.model.ListEmpresa
import br.senai.sp.jandira.mesausers.model.ListFavoritosResponse
import br.senai.sp.jandira.mesausers.model.Pedido
import br.senai.sp.jandira.mesausers.model.PedidoResponse
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.CardAlimento
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.backgroundLight
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import br.senai.sp.jandira.mesausers.ui.theme.primaryLight
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

fun formatarDataSegura(dataString: String?): String {
    if (dataString == null) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dataString)
        date?.let { outputFormat.format(it) } ?: dataString
    } catch (e: Exception) {
        dataString // Retorna a string original se houver erro
    }
}

@Composable
fun InstituicaoScreen(
    navegacao: NavHostController?, 
    empresaId: Int = -1,  // Default value for when no ID is provided
    sharedViewModel: SharedViewModel
) {

    // Estados da UI
    var empresa = remember { mutableStateOf<Empresa?>(null) }
    var alimentosList = remember { mutableStateOf<List<AlimentoFiltro>>(emptyList()) }
    var isLoading = remember { mutableStateOf(true) }
    var errorMessage = remember { mutableStateOf<String?>(null) }
    var isFavorited = remember { mutableStateOf(false) } // Estado para favoritar

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Função para criar o pedido
    fun criarPedido(idAlimento: Int) {
        val pedido = if (sharedViewModel.tipo_usuario.equals("ong", ignoreCase = true)) {
            Pedido(id_ong = sharedViewModel.id_ong, id_alimento = idAlimento, quantidade = 1)
        } else {
            Pedido(id_usuario = sharedViewModel.id_usuario, id_alimento = idAlimento, quantidade = 1)
        }

        val call = RetrofitFactory().getPedidoService().criarPedido(pedido)

        call.enqueue(object : Callback<PedidoResponse> {
            override fun onResponse(call: Call<PedidoResponse>, response: Response<PedidoResponse>) {
                val pedidoResponse = response.body()
                if (response.isSuccessful && pedidoResponse != null && pedidoResponse.status) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Pedido acrescentado com sucesso!")
                    }
                } else {
                    scope.launch {
                        val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                        Log.e("InstituicaoScreen", "Erro ao criar pedido: ${response.code()} - $errorBody")
                        snackbarHostState.showSnackbar("Erro ao criar pedido: ${pedidoResponse?.message ?: response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<PedidoResponse>, t: Throwable) {
                scope.launch {
                    Log.e("InstituicaoScreen", "Falha na conexão ao criar pedido", t)
                    snackbarHostState.showSnackbar("Falha na conexão. Tente novamente mais tarde.")
                }
            }
        })
    }

    fun carregarFavoritos() {
        val call = RetrofitFactory().getFavoritoService().getFavoritos(
            mapOf(
                if (sharedViewModel.tipo_usuario.equals("ong", ignoreCase = true)) {
                    "id_ong" to sharedViewModel.id_ong.toString()
                } else {
                    "id_usuario" to sharedViewModel.id_usuario.toString()
                }
            )
        )

        call.enqueue(object : Callback<ListFavoritosResponse> {
            override fun onResponse(call: Call<ListFavoritosResponse>, response: Response<ListFavoritosResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        if (responseBody.status) {
                            sharedViewModel.updateFavoritos(responseBody.favoritos)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ListFavoritosResponse>, t: Throwable) {
                Log.e("InstituicaoScreen", "Falha ao carregar favoritos", t)
            }
        })
    }

    fun criarFavorito(empresaId: Int) {
        val favorito = if (sharedViewModel.tipo_usuario.equals("ong", ignoreCase = true)) {
            Favorito(id_ong = sharedViewModel.id_ong, id_empresa = empresaId)
        } else {
            Favorito(id_usuario = sharedViewModel.id_usuario, id_empresa = empresaId)
        }

        val call = RetrofitFactory().getFavoritoService().criarFavorito(favorito)

        call.enqueue(object : Callback<FavoritoResponse> {
            override fun onResponse(call: Call<FavoritoResponse>, response: Response<FavoritoResponse>) {
                val favoritoResponse = response.body()
                if (response.isSuccessful && favoritoResponse != null && favoritoResponse.status) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Empresa adicionada aos favoritos!")
                        // Atualiza a lista de favoritos após adicionar um novo
                        carregarFavoritos()
                    }
                } else {
                    scope.launch {
                        val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                        Log.e("InstituicaoScreen", "Erro ao favoritar empresa: ${response.code()} - $errorBody")
                        snackbarHostState.showSnackbar("Erro ao favoritar empresa: ${favoritoResponse?.message ?: response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<FavoritoResponse>, t: Throwable) {
                scope.launch {
                    Log.e("InstituicaoScreen", "Falha na conexão ao favoritar empresa", t)
                    snackbarHostState.showSnackbar("Falha na conexão. Tente novamente mais tarde.")
                }
            }
        })
    }

    // Função para carregar detalhes da empresa
    fun carregarEmpresa() {
        isLoading.value = true
        val call = RetrofitFactory().getUserService().listEmpresa()

        call.enqueue(object : Callback<ListEmpresa> {
            override fun onResponse(call: Call<ListEmpresa>, response: Response<ListEmpresa>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { listEmpresa ->
                        val empresaEncontrada = listEmpresa.empresas.find { it.id == empresaId }
                        if (empresaEncontrada != null) {
                            if (empresaEncontrada.nome != null && empresaEncontrada.nome.isNotBlank()) {
                                empresa.value = empresaEncontrada
                            } else {
                                errorMessage.value = "Empresa encontrada mas sem dados cadastrados"
                                Log.w("InstituicaoScreen", "Empresa com ID $empresaId encontrada mas sem dados válidos")
                            }
                        } else {
                            errorMessage.value = "Empresa não encontrada"
                            Log.w("InstituicaoScreen", "Empresa com ID $empresaId não encontrada na lista")
                        }
                    } ?: run {
                        errorMessage.value = "Falha ao carregar lista de empresas"
                        Log.w("InstituicaoScreen", "Response body da lista de empresas é nulo")
                    }
                } else {
                    errorMessage.value = "Falha ao carregar empresas"
                    Log.e("InstituicaoScreen", "Erro ao carregar lista de empresas: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ListEmpresa>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Falha na conexão"
                Log.e("InstituicaoScreen", "Erro na requisição da lista de empresas", t)
                Log.e("InstituicaoScreen", "Mensagem do erro: ${t.message}")
            }
        })
    }

    // Função para carregar alimentos da empresa
    fun carregarAlimentosEmpresa() {
        val call = RetrofitFactory().getAlimentoService().filtroEmpresa(empresaId)

        call.enqueue(object : Callback<ListAlimentoFiltro> {
            override fun onResponse(call: Call<ListAlimentoFiltro>, response: Response<ListAlimentoFiltro>) {
                if (response.isSuccessful) {
                    response.body()?.let { listAlimentoFiltro ->
                        alimentosList.value = listAlimentoFiltro.resultFiltro ?: emptyList()
                        Log.d("InstituicaoScreen", "Alimentos da empresa carregados: ${listAlimentoFiltro.resultFiltro?.size ?: 0}")
                    } ?: run {
                        alimentosList.value = emptyList()
                        Log.w("InstituicaoScreen", "Response body de alimentos é nulo")
                    }
                } else {
                    if (response.code() == 404) {
                        alimentosList.value = emptyList()
                        Log.d("InstituicaoScreen", "Nenhum alimento encontrado para a empresa (404)")
                    } else {
                        Log.e("InstituicaoScreen", "Erro ao carregar alimentos: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<ListAlimentoFiltro>, t: Throwable) {
                alimentosList.value = emptyList()
                Log.e("InstituicaoScreen", "Erro na requisição de alimentos", t)
            }
        })
    }

    // Carregar dados quando a tela for criada
    LaunchedEffect(empresaId) {
        carregarEmpresa()
        carregarAlimentosEmpresa()
    }

    Scaffold(
        topBar = {
            BarraDeTitulo()
        },
        bottomBar = {
            BarraInferior(navegacao)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundLight)
                    .padding(paddingValues)
            ) {
                when {
                    isLoading.value -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage.value != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = errorMessage.value ?: "Erro desconhecido",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = poppinsFamily,
                                    color = primaryLight,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    empresa.value != null -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header da empresa
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Logo da empresa
                                        Card(
                                            modifier = Modifier.size(120.dp),
                                            shape = CircleShape,
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            AsyncImage(
                                                model = empresa.value?.foto ?: "",
                                                contentDescription = "Logo da ${empresa.value?.nome}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Fit
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Nome da empresa e botão de favoritar
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (empresa.value?.nome.isNullOrEmpty()) "Nome não informado" else empresa.value?.nome ?: "Nome não informado",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = poppinsFamily,
                                                color = primaryLight,
                                                modifier = Modifier.weight(1f)
                                            )

                                            IconButton(
                                                onClick = {
                                                    isFavorited.value = !isFavorited.value
                                                    criarFavorito(empresaId)
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (isFavorited.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = if (isFavorited.value) "Remover dos favoritos" else "Adicionar aos favoritos",
                                                    tint = if (isFavorited.value) Color.Red else primaryLight,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Distância (placeholder - você pode calcular baseado na localização)
                                        Text(
                                            text = "5km", // Placeholder
                                            fontSize = 16.sp,
                                            fontFamily = poppinsFamily,
                                            color = Color.Gray,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Endereço
                                        Column(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Endereço:",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = poppinsFamily,
                                                color = primaryLight
                                            )
                                            Text(
                                                text = if (empresa.value?.endereco.isNullOrEmpty()) "Endereço não informado" else empresa.value?.endereco ?: "Endereço não informado",
                                                fontSize = 14.sp,
                                                fontFamily = poppinsFamily,
                                                color = Color.Gray
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Contato
                                        Column(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Contato:",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = poppinsFamily,
                                                color = primaryLight
                                            )
                                            Text(
                                                text = if (empresa.value?.telefone.isNullOrEmpty()) "Telefone não informado" else empresa.value?.telefone ?: "Telefone não informado",
                                                fontSize = 14.sp,
                                                fontFamily = poppinsFamily,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }

                            // Lista de alimentos da empresa
                            if (alimentosList.value.isNotEmpty()) {
                                items(alimentosList.value) { alimento ->
                                    CardAlimento(
                                        id = alimento.id ?: 0,
                                        img = alimento.imagem ?: "",
                                        nome = alimento.nome ?: "Alimento sem nome",
                                        prazo = formatarDataSegura(alimento.prazo),
                                        quantidade = alimento.quantidade ?: "0",
                                        imgEmpresa = alimento.fotoEmpresa ?: "",
                                        empresa = alimento.nomeEmpresa ?: "Empresa não informada",
                                        onClick = { idAlimento -> criarPedido(idAlimento) }
                                    )
                                }
                            } else {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "🍽️",
                                                fontSize = 48.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Esta empresa ainda não cadastrou alimentos",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                fontFamily = poppinsFamily,
                                                color = primaryLight,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Aguarde novos cadastros",
                                                fontSize = 14.sp,
                                                fontFamily = poppinsFamily,
                                                color = Color.Gray.copy(alpha = 0.8f),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
