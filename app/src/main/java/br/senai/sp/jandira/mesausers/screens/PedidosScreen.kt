package br.senai.sp.jandira.mesausers.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.mesausers.model.ListPedidosResponse
import br.senai.sp.jandira.mesausers.model.PedidoUsuario
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.CardPedido
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.backgroundLight
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import br.senai.sp.jandira.mesausers.ui.theme.primaryLight
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun PedidosScreen(navegacao: NavHostController?, sharedViewModel: SharedViewModel) {

    val pedidosState = remember { mutableStateOf<List<PedidoUsuario>?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val idKey = if (sharedViewModel.tipo_usuario.equals("ong", ignoreCase = true)) "id_ong" else "id_usuario"
        val idValue = if (sharedViewModel.tipo_usuario.equals("ong", ignoreCase = true)) sharedViewModel.id_ong else sharedViewModel.id_usuario

        if (idValue == 0) {
            errorMessage.value = "ID do usuário não encontrado. Faça login novamente."
            isLoading.value = false
            return@LaunchedEffect
        }

        val params = mapOf(idKey to idValue.toString())

        val call = RetrofitFactory().getPedidoService().getPedidos(params)

        call.enqueue(object : Callback<ListPedidosResponse> {
            override fun onResponse(call: Call<ListPedidosResponse>, response: Response<ListPedidosResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val listPedidosResponse = response.body()
                    if (listPedidosResponse != null && listPedidosResponse.status) {
                        pedidosState.value = listPedidosResponse.pedidos
                        if (listPedidosResponse.pedidos.isNullOrEmpty()) {
                            errorMessage.value = "Você ainda não fez nenhum pedido."
                        }
                    } else {
                        errorMessage.value = listPedidosResponse?.message ?: "Erro ao buscar pedidos."
                    }
                } else {
                    errorMessage.value = "Erro de servidor: ${response.code()}"
                    Log.e("PedidosScreen", "Erro na resposta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ListPedidosResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Falha na conexão. Verifique sua internet."
                Log.e("PedidosScreen", "Falha na requisição", t)
            }
        })
    }

    Scaffold(
        topBar = { BarraDeTitulo() },
        bottomBar = { BarraInferior(navegacao) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundLight)
                    .padding(it)
            ) {
                when {
                    isLoading.value -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = primaryLight)
                        }
                    }
                    errorMessage.value != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = errorMessage.value!!,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = poppinsFamily,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                    pedidosState.value != null -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Meus Pedidos",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = poppinsFamily,
                                    color = primaryLight
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            items(pedidosState.value!!) { pedido ->
                                CardPedido(
                                    alimento = pedido.nomeAlimento,
                                    imagem = pedido.imagemAlimento,
                                    quantidade = pedido.quantidadePedido.toString(),
                                    empresa = pedido.nomeEmpresa,
                                    validade = pedido.dataValidade,
                                    onDelete = { /* TODO: Implementar exclusão */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}