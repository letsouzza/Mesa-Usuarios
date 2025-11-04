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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.Alimento
import br.senai.sp.jandira.mesausers.model.Categoria
import br.senai.sp.jandira.mesausers.model.Empresa
import br.senai.sp.jandira.mesausers.model.ListAlimento
import br.senai.sp.jandira.mesausers.model.ListCategoria
import br.senai.sp.jandira.mesausers.model.ListEmpresa
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


// Função para formatar a data de yyyy-MM-dd para dd/MM/yy
fun formatarData(dataOriginal: String): String {
    return try {
        val formatoOriginal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatoDesejado = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val data = formatoOriginal.parse(dataOriginal)
        formatoDesejado.format(data ?: return dataOriginal)
    } catch (e: Exception) {
        dataOriginal // Retorna a data original se houver erro
    }
}

@Composable
fun HomeScreen(navegacao: NavHostController?) {

    var controleNavegacao = rememberNavController()

    // Estados para controlar a UI
    var alimentoList = remember {
        mutableStateOf(listOf<Alimento>())
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
    var isLoading = remember {
        mutableStateOf(true)
    }
    var errorMessage = remember {
        mutableStateOf<String?>(null)
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

        // Carregar alimentos
        val callRetrofit = RetrofitFactory()
            .getAlimentoService()
            .listAlimento()

        callRetrofit.enqueue(object : Callback<ListAlimento> {
            override fun onResponse(call: Call<ListAlimento>, response: Response<ListAlimento>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { listAlimento ->
                        alimentoList.value = listAlimento.alimentos
                        Log.d("HomeScreen", "Alimentos carregados: ${listAlimento.alimentos.size}")
                    }
                } else {
                    errorMessage.value = "Erro ao carregar alimentos: ${response.code()}"
                    Log.e("HomeScreen", "Erro na resposta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ListAlimento>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Erro de conexão: ${t.message}"
                Log.e("HomeScreen", "Erro na requisição", t)
            }
        })
    }

    Scaffold (
        topBar = {
            BarraDeTitulo()
        },
        bottomBar = {
            BarraInferior(controleNavegacao)
        },
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
                    DropdownFiltros()
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
                                onClick = { categoriaSelecionada.value = 0 },
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
                                onClick = { categoriaSelecionada.value = categoria.id },
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
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage.value ?: "Erro desconhecido",
                                color = Color.Red
                            )
                        }
                    }
                    alimentoList.value.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Nenhum alimento encontrado")
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(alimentoList.value) { alimento ->
                                CardAlimento(
                                    img = alimento.imagem,
                                    nome = alimento.nome,
                                    prazo = formatarData(alimento.prazo),
                                    quantidade = alimento.quantidade,
                                    imgEmpresa = "",
                                    empresa = "Atacadão"
                                )
                            }
                        }
                    }
                }

                // Seção de Instituições
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
                                imagem = empresa.foto
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
        HomeScreen(null)
    }
}