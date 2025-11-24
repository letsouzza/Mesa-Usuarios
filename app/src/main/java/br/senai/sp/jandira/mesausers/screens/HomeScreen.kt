package br.senai.sp.jandira.mesausers.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Locale
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.Alimento
import br.senai.sp.jandira.mesausers.model.AlimentoFiltro
import br.senai.sp.jandira.mesausers.model.Categoria
import br.senai.sp.jandira.mesausers.model.Empresa
import br.senai.sp.jandira.mesausers.model.ListAlimento
import br.senai.sp.jandira.mesausers.model.ListAlimentoFiltro
import br.senai.sp.jandira.mesausers.model.ListCategoria
import br.senai.sp.jandira.mesausers.model.ListEmpresa
import br.senai.sp.jandira.mesausers.model.Pedido
import br.senai.sp.jandira.mesausers.model.PedidoResponse
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.CardAlimento
import br.senai.sp.jandira.mesausers.screens.components.CardInstituicao
import br.senai.sp.jandira.mesausers.screens.components.DropdownFiltros
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.MesaTheme
import br.senai.sp.jandira.mesausers.ui.theme.backgroundLight
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import br.senai.sp.jandira.mesausers.ui.theme.primaryLight
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// Função para formatar a data de yyyy-MM-dd para dd/MM/yy
fun formatarData(dataOriginal: String?): String {
    return try {
        if (dataOriginal.isNullOrEmpty()) {
            return "Data não informada"
        }
        val formatoOriginal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoDesejado = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val data = formatoOriginal.parse(dataOriginal)
        formatoDesejado.format(data ?: return dataOriginal)
    } catch (e: Exception) {
        Log.e("HomeScreen", "Erro ao formatar data: $dataOriginal", e)
        dataOriginal ?: "Data inválida"
    }
}

@Composable
fun HomeScreen(navegacao: NavHostController?, sharedViewModel: SharedViewModel) {

    // Estados para controlar a UI
    var alimentoList = remember {
        mutableStateOf(listOf<Alimento>())
    }
    var alimentoListFiltro = remember {
        mutableStateOf(listOf<AlimentoFiltro>())
    }
    var categoriaList = remember {
        mutableStateOf(listOf<Categoria>())
    }
    var empresaList = remember {
        mutableStateOf(listOf<Empresa>())
    }
    var categoriaSelecionada = remember {
        mutableStateOf(0) // 0 = All
    }
    var empresaSelecionada = remember {
        mutableStateOf(0) // 0 = Nenhuma empresa selecionada
    }
    var dataSelecionada = remember {
        mutableStateOf("") // String vazia = Nenhuma data selecionada
    }
    var isLoading = remember {
        mutableStateOf(true)
    }
    var errorMessage = remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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

    // Função para carregar alimentos (todos ou por categoria)
    fun carregarAlimentos(categoriaId: Int = 0) {
        isLoading.value = true
        errorMessage.value = null

        if (categoriaId == 0) {
            // Carregar todos os alimentos
            val call = RetrofitFactory().getAlimentoService().listAlimento()

            call.enqueue(object : Callback<ListAlimento> {
                override fun onResponse(call: Call<ListAlimento>, response: Response<ListAlimento>) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        response.body()?.let { listAlimento ->
                            alimentoList.value = listAlimento.alimentos ?: emptyList()
                            alimentoListFiltro.value = emptyList() // Limpar lista filtrada
                            Log.d("HomeScreen", "Alimentos carregados: ${listAlimento.alimentos?.size ?: 0}")
                        } ?: run {
                            alimentoList.value = emptyList()
                            alimentoListFiltro.value = emptyList()
                            Log.w("HomeScreen", "Response body é nulo")
                        }
                    } else {
                        // Tratar 404 como lista vazia (sem erro)
                        alimentoList.value = emptyList()
                        alimentoListFiltro.value = emptyList()
                        if (response.code() == 404) {
                            errorMessage.value = null
                            Log.i("HomeScreen", "Nenhum alimento disponível (404)")
                        } else {
                            errorMessage.value = "Falha no carregamento"
                            Log.e("HomeScreen", "Erro na resposta: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<ListAlimento>, t: Throwable) {
                    isLoading.value = false
                    alimentoList.value = emptyList()
                    alimentoListFiltro.value = emptyList()
                    errorMessage.value = "Falha na conexão"
                    Log.e("HomeScreen", "Erro na requisição", t)
                }
            })
        } else {
            // Carregar alimentos por categoria
            val call = RetrofitFactory().getAlimentoService().filtroCategoria(categoriaId)

            call.enqueue(object : Callback<ListAlimentoFiltro> {
                override fun onResponse(call: Call<ListAlimentoFiltro>, response: Response<ListAlimentoFiltro>) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        response.body()?.let { listAlimentoFiltro ->
                            alimentoListFiltro.value = listAlimentoFiltro.resultFiltro ?: emptyList()
                            alimentoList.value = emptyList() // Limpar lista geral
                            Log.d("HomeScreen", "Alimentos filtrados carregados: ${listAlimentoFiltro.resultFiltro?.size ?: 0}")
                        } ?: run {
                            alimentoListFiltro.value = emptyList()
                            alimentoList.value = emptyList()
                            Log.w("HomeScreen", "Response body é nulo")
                        }
                    } else {
                        alimentoListFiltro.value = emptyList()
                        alimentoList.value = emptyList()
                        // Tratar 404 como "nenhum alimento disponível"
                        if (response.code() == 404) {
                            errorMessage.value = null
                            Log.i("HomeScreen", "Nenhum alimento encontrado para a categoria (404)")
                        } else {
                            errorMessage.value = "Falha no carregamento"
                            Log.e("HomeScreen", "Erro na resposta: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<ListAlimentoFiltro>, t: Throwable) {
                    isLoading.value = false
                    alimentoListFiltro.value = emptyList()
                    alimentoList.value = emptyList()
                    errorMessage.value = "Falha na conexão"
                    Log.e("HomeScreen", "Erro na requisição", t)
                }
            })
        }
    }

    // Função para carregar alimentos por empresa
    fun carregarAlimentosPorEmpresa(empresaId: Int) {
        isLoading.value = true
        errorMessage.value = null

        val call = RetrofitFactory().getAlimentoService().filtroEmpresa(empresaId)

        call.enqueue(object : Callback<ListAlimentoFiltro> {
            override fun onResponse(call: Call<ListAlimentoFiltro>, response: Response<ListAlimentoFiltro>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { listAlimentoFiltro ->
                        alimentoListFiltro.value = listAlimentoFiltro.resultFiltro ?: emptyList()
                        alimentoList.value = emptyList() // Limpar lista geral
                        Log.d("HomeScreen", "Alimentos da empresa carregados: ${listAlimentoFiltro.resultFiltro?.size ?: 0}")
                    } ?: run {
                        alimentoListFiltro.value = emptyList()
                        alimentoList.value = emptyList()
                        Log.w("HomeScreen", "Response body é nulo")
                    }
                } else {
                    alimentoListFiltro.value = emptyList()
                    alimentoList.value = emptyList()
                    // Tratar 404 como "nenhum alimento disponível"
                    if (response.code() == 404) {
                        errorMessage.value = null
                        Log.i("HomeScreen", "Nenhum alimento encontrado para a empresa (404)")
                    } else {
                        errorMessage.value = "Falha no carregamento"
                        Log.e("HomeScreen", "Erro na resposta: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<ListAlimentoFiltro>, t: Throwable) {
                isLoading.value = false
                alimentoListFiltro.value = emptyList()
                alimentoList.value = emptyList()
                errorMessage.value = "Falha na conexão"
                Log.e("HomeScreen", "Erro na requisição", t)
            }
        })
    }

    // Função para carregar alimentos por data
    fun carregarAlimentosPorData(data: String) {
        isLoading.value = true
        errorMessage.value = null

        Log.d("HomeScreen", "Iniciando filtro por data: '$data'")
        val call = RetrofitFactory().getAlimentoService().filtroData(data)

        call.enqueue(object : Callback<ListAlimentoFiltro> {
            override fun onResponse(call: Call<ListAlimentoFiltro>, response: Response<ListAlimentoFiltro>) {
                isLoading.value = false
                Log.d("HomeScreen", "Resposta da API filtroData - Código: ${response.code()}")
                Log.d("HomeScreen", "Headers da resposta: ${response.headers()}")

                if (response.isSuccessful) {
                    response.body()?.let { listAlimentoFiltro ->
                        Log.d("HomeScreen", "Response body recebido: $listAlimentoFiltro")
                        Log.d("HomeScreen", "ResultFiltro: ${listAlimentoFiltro.resultFiltro}")
                        alimentoListFiltro.value = listAlimentoFiltro.resultFiltro ?: emptyList()
                        alimentoList.value = emptyList() // Limpar lista geral
                        Log.d("HomeScreen", "Alimentos por data carregados: ${listAlimentoFiltro.resultFiltro?.size ?: 0}")

                        // Log detalhado de cada alimento encontrado
                        listAlimentoFiltro.resultFiltro?.forEachIndexed { index, alimento ->
                            Log.d("HomeScreen", "Alimento $index: nome=${alimento.nome}, prazo=${alimento.prazo}")
                        }
                    } ?: run {
                        alimentoListFiltro.value = emptyList()
                        alimentoList.value = emptyList()
                        Log.w("HomeScreen", "Response body é nulo")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("HomeScreen", "Erro na resposta da API filtroData:")
                    Log.e("HomeScreen", "Código: ${response.code()}")
                    Log.e("HomeScreen", "Mensagem: ${response.message()}")
                    Log.e("HomeScreen", "Error body: $errorBody")

                    alimentoListFiltro.value = emptyList()
                    alimentoList.value = emptyList()
                    // Tratar 404 como "nenhum alimento disponível"
                    if (response.code() == 404) {
                        errorMessage.value = null
                        Log.i("HomeScreen", "Nenhum alimento encontrado para a data (404)")
                    } else {
                        errorMessage.value = "Falha no carregamento"
                        Log.e("HomeScreen", "Erro na resposta: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<ListAlimentoFiltro>, t: Throwable) {
                isLoading.value = false
                alimentoListFiltro.value = emptyList()
                alimentoList.value = emptyList()
                errorMessage.value = "Falha na conexão"
                Log.e("HomeScreen", "Erro na requisição por data", t)
            }
        })
    }

    // Carregar dados da API quando a tela for criada
    LaunchedEffect(Unit) {
        // Carregar categorias
        val callCategoria = RetrofitFactory()
            .getAlimentoService()
            .listCategoria()

        callCategoria.enqueue(object : Callback<ListCategoria> {
            override fun onResponse(call: Call<ListCategoria>, response: Response<ListCategoria>) {
                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        categoriaList.value = result.categorias ?: emptyList()
                    }
                }
            }
            override fun onFailure(call: Call<ListCategoria>, t: Throwable) {
                Log.e("HomeScreen", "Erro ao carregar categorias", t)
            }
        })

        // Carregar empresas
        val callEmpresa = RetrofitFactory()
            .getUserService()
            .listEmpresa()

        callEmpresa.enqueue(object : Callback<ListEmpresa> {
            override fun onResponse(call: Call<ListEmpresa>, response: Response<ListEmpresa>) {
                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        empresaList.value = result.empresas
                        Log.d("HomeScreen", "Empresas carregadas: ${result.empresas.size}")
                    }
                } else {
                    Log.e("HomeScreen", "Erro ao carregar empresas: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ListEmpresa>, t: Throwable) {
                Log.e("HomeScreen", "Erro na requisição de empresas", t)
            }
        })

        // Carregar todos os alimentos inicialmente
        carregarAlimentos(0)
    }

    Scaffold (
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
                // Seção de Filtros
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    DropdownFiltros(
                        onEmpresaSelecionada = { empresaId ->
                            empresaSelecionada.value = empresaId
                            categoriaSelecionada.value = 0 // Reset categoria
                            dataSelecionada.value = "" // Reset data
                            carregarAlimentosPorEmpresa(empresaId)
                        },
                        onDataSelecionada = { data ->
                            dataSelecionada.value = data
                            categoriaSelecionada.value = 0 // Reset categoria
                            empresaSelecionada.value = 0 // Reset empresa
                            carregarAlimentosPorData(data)
                        }
                    )
                }

                // Seção de Categorias
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.categorias),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFamily,
                        color = primaryLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        // Botão "All"
                        item {
                            Button(
                                onClick = {
                                    categoriaSelecionada.value = 0
                                    empresaSelecionada.value = 0 // Reset empresa
                                    dataSelecionada.value = "" // Reset data
                                    carregarAlimentos(0)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (categoriaSelecionada.value == 0) primaryLight else Color.Gray,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.all),
                                    fontFamily = poppinsFamily
                                )
                            }
                        }

                        // Categorias da API
                        items(categoriaList.value) { categoria ->
                            Button(
                                onClick = {
                                    categoriaSelecionada.value = categoria.id
                                    empresaSelecionada.value = 0 // Reset empresa
                                    dataSelecionada.value = "" // Reset data
                                    carregarAlimentos(categoria.id)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (categoriaSelecionada.value == categoria.id) primaryLight else Color.Gray,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = categoria.nome,
                                    fontFamily = poppinsFamily
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de Alimentos
                Box(
                    modifier = Modifier.weight(1f)
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
                            // Mensagem de erro no carregamento
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
                                        text = "Não foi possível carregar os alimentos",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = poppinsFamily,
                                        color = primaryLight,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Verifique sua conexão e tente novamente",
                                        fontSize = 14.sp,
                                        fontFamily = poppinsFamily,
                                        color = Color.Gray.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                        (alimentoList.value.isEmpty() && alimentoListFiltro.value.isEmpty() && errorMessage.value == null) -> {
                            // Mensagem quando não há alimentos na categoria
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
                                        text = "🍽️",
                                        fontSize = 48.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = when {
                                            categoriaSelecionada.value == 0 && empresaSelecionada.value == 0 -> "Nenhum alimento disponível"
                                            categoriaSelecionada.value != 0 -> "Não há alimentos desta categoria"
                                            empresaSelecionada.value != 0 -> "Não há alimentos desta empresa"
                                            else -> "Nenhum alimento encontrado"
                                        },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = poppinsFamily,
                                        color = primaryLight,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = when {
                                            categoriaSelecionada.value != 0 -> "Tente selecionar outra categoria"
                                            empresaSelecionada.value != 0 -> "Tente selecionar outra empresa"
                                            else -> "Tente selecionar uma categoria ou empresa"
                                        },
                                        fontSize = 14.sp,
                                        fontFamily = poppinsFamily,
                                        color = Color.Gray.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                // Mostrar alimentos gerais ou filtrados
                                if (categoriaSelecionada.value == 0 && empresaSelecionada.value == 0 && dataSelecionada.value.isEmpty()) {
                                    // Mostrar todos os alimentos (sem filtros)
                                    Log.d("HomeScreen", "Renderizando todos os alimentos: ${alimentoList.value.size}")
                                    items(alimentoList.value) { alimento ->
                                        CardAlimento(
                                            id = alimento.id ?: 0,
                                            img = alimento.imagem ?: "",
                                            nome = alimento.nome ?: "Alimento sem nome",
                                            prazo = formatarData(alimento.prazo),
                                            quantidade = alimento.quantidade ?: "0",
                                            imgEmpresa = alimento.empresa?.foto ?: "",
                                            empresa = alimento.empresa?.nome ?: "Empresa não informada",
                                            onClick = { idAlimento -> criarPedido(idAlimento) }
                                        )
                                    }
                                } else {
                                    // Mostrar alimentos filtrados (por categoria, empresa ou data)
                                    Log.d("HomeScreen", "Renderizando alimentos filtrados: ${alimentoListFiltro.value.size}")
                                    Log.d("HomeScreen", "Filtros ativos - Categoria: ${categoriaSelecionada.value}, Empresa: ${empresaSelecionada.value}, Data: '${dataSelecionada.value}'")
                                    items(alimentoListFiltro.value) { alimento ->
                                        CardAlimento(
                                            id = alimento.id ?: 0,
                                            img = alimento.imagem ?: "",
                                            nome = alimento.nome ?: "Alimento sem nome",
                                            prazo = formatarData(alimento.prazo),
                                            quantidade = alimento.quantidade ?: "0",
                                            imgEmpresa = alimento.fotoEmpresa ?: "",
                                            empresa = alimento.nomeEmpresa ?: "Empresa não informada",
                                            onClick = { idAlimento -> criarPedido(idAlimento) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Seção de Instituições - sempre visível
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.instituicoes),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsFamily,
                        color = primaryLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(empresaList.value) { empresa ->
                            CardInstituicao(
                                nome = empresa.nome,
                                imagem = empresa.foto,
                                onClick = {
                                    navegacao?.navigate("instituicao/${empresa.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomeScreenPreview() {
    MesaTheme {
        HomeScreen(null, viewModel())
    }
}
