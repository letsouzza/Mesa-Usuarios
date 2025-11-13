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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import br.senai.sp.jandira.mesausers.model.AlimentoFiltro
import br.senai.sp.jandira.mesausers.model.Empresa
import br.senai.sp.jandira.mesausers.model.ListAlimentoFiltro
import br.senai.sp.jandira.mesausers.model.ListEmpresa
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.CardAlimento
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.backgroundLight
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import br.senai.sp.jandira.mesausers.ui.theme.primaryLight
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Função para formatar data de forma segura
fun formatarDataSegura(dataOriginal: String?): String {
    if (dataOriginal.isNullOrEmpty() || dataOriginal.isBlank()) {
        return "Data não informada"
    }
    
    // Se a data está no formato yyyy-MM-dd, converte para dd/MM/yyyy
    return try {
        if (dataOriginal.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            val partes = dataOriginal.split("-")
            if (partes.size == 3) {
                "${partes[2]}/${partes[1]}/${partes[0]}"
            } else {
                dataOriginal
            }
        } else {
            dataOriginal
        }
    } catch (e: Exception) {
        dataOriginal
    }
}

@Composable
fun InstituicaoScreen(
    navegacao: NavHostController?,
    empresaId: Int
) {
    // Estados para controlar a UI
    var empresa = remember { mutableStateOf<Empresa?>(null) }
    var alimentosList = remember { mutableStateOf(listOf<AlimentoFiltro>()) }
    var isLoading = remember { mutableStateOf(true) }
    var errorMessage = remember { mutableStateOf<String?>(null) }
    var isFavorited = remember { mutableStateOf(false) }

    // Função para carregar dados da empresa usando a lista de empresas
    fun carregarEmpresa() {
        isLoading.value = true
        errorMessage.value = null
        
        Log.d("InstituicaoScreen", "Iniciando carregamento da empresa com ID: $empresaId")
        
        // Buscar na lista de empresas
        RetrofitFactory().getUserService().listEmpresa().enqueue(object : Callback<ListEmpresa> {
            override fun onResponse(call: Call<ListEmpresa>, response: Response<ListEmpresa>) {
                isLoading.value = false
                
                if (response.isSuccessful) {
                    response.body()?.let { listEmpresa ->
                        
                        // Buscar a empresa específica pelo ID
                        val empresaEncontrada = listEmpresa.empresas.find { it.id == empresaId }
                        
                        if (empresaEncontrada != null) {
                            // Verificar se a empresa tem dados válidos
                            if (empresaEncontrada.id != 0 && empresaEncontrada.nome.isNotBlank()) {
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
                                                    // TODO: Implementar lógica de favoritar na API
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
                                        img = alimento.imagem ?: "",
                                        nome = alimento.nome ?: "Alimento sem nome",
                                        prazo = formatarDataSegura(alimento.prazo),
                                        quantidade = alimento.quantidade ?: "0",
                                        imgEmpresa = alimento.fotoEmpresa ?: "",
                                        empresa = alimento.nomeEmpresa ?: "Empresa não informada"
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
