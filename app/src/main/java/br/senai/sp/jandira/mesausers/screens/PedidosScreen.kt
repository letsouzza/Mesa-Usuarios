package br.senai.sp.jandira.mesausers.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import br.senai.sp.jandira.mesausers.model.ResponseGeral
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import br.senai.sp.jandira.mesausers.util.showToast
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
import retrofit2.awaitResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(navegacao: NavHostController?, sharedViewModel: SharedViewModel) {
    // Estado para controlar o diálogo de confirmação
    val showDeleteDialog = remember { mutableStateOf(false) }
    // Estado para armazenar o pedido a ser excluído
    val pedidoParaExcluir = remember { mutableStateOf<PedidoUsuario?>(null) }

    val pedidosState by sharedViewModel.pedidos.observeAsState(initial = null)
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage by sharedViewModel.errorMessage.observeAsState(initial = null)

    // Função para carregar os pedidos
    fun carregarPedidos() {
        isLoading.value = true
        // Busca os pedidos do usuário
        val call = RetrofitFactory().getPedidoService().getPedidos(
            mapOf("id_usuario" to sharedViewModel.id_usuario.toString())
        )
        
        call.enqueue(object : Callback<ListPedidosResponse> {
            override fun onResponse(call: Call<ListPedidosResponse>, response: Response<ListPedidosResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        sharedViewModel.updatePedidos(responseBody.pedidos)

                        val pedidos = responseBody.pedidos
                        if (pedidos == null || pedidos.isEmpty()) {
                            sharedViewModel.setErrorMessage("Você ainda não fez nenhum pedido.")
                        } else {
                            sharedViewModel.setErrorMessage(null)
                        }
                    } else {
                        sharedViewModel.setErrorMessage(responseBody?.message ?: "Erro ao carregar pedidos")
                    }
                } else {
                    sharedViewModel.setErrorMessage("Erro de servidor: ${response.code()}")
                    Log.e("PedidosScreen", "Erro na resposta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ListPedidosResponse>, t: Throwable) {
                isLoading.value = false
                sharedViewModel.setErrorMessage("Falha na conexão. Verifique sua internet.")
                Log.e("PedidosScreen", "Falha na requisição", t)
            }
        })
    }

    // Carrega os pedidos quando a tela é exibida
    LaunchedEffect(Unit) {
        carregarPedidos()
    }

    fun deletarPedido(
        idPedido: Int,
        position: Int,
        sharedViewModel: SharedViewModel,
        context: android.content.Context,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val call = RetrofitFactory().getPedidoService().deletarPedido(idPedido)
        
        call.enqueue(object : Callback<ResponseGeral> {
            override fun onResponse(call: Call<ResponseGeral>, response: Response<ResponseGeral>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        // Atualiza a lista local removendo o item excluído
                        val currentList = sharedViewModel.pedidos.value?.toMutableList() ?: mutableListOf()
                        if (position in currentList.indices) {
                            currentList.removeAt(position)
                            sharedViewModel.updatePedidos(currentList.toList())
                        }
                        
                        Toast.makeText(
                            context,
                            responseBody.message ?: "Pedido removido com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        onSuccess()
                        
                        if (currentList.isNullOrEmpty()) {
                            sharedViewModel.setErrorMessage("Você ainda não fez nenhum pedido.")
                        }
                    } else {
                        val errorMsg = responseBody?.message ?: "Erro ao remover pedido"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        onError(errorMsg)
                    }
                } else {
                    val errorMsg = "Erro ao remover pedido: ${response.code()}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ResponseGeral>, t: Throwable) {
                val errorMsg = "Falha na conexão. Verifique sua internet."
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                Log.e("PedidosScreen", "Falha ao deletar pedido", t)
                onError(errorMsg)
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
                    errorMessage != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = errorMessage!!,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = poppinsFamily,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                    pedidosState!= null -> {
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
                            items(pedidosState!!) { pedido ->
                                val position = pedidosState?.indexOfFirst { it.idPedido == pedido.idPedido } ?: -1
                                // Add this inside your items loop in PedidosScreen.kt
                                Log.d("PedidosScreen", "Image URL for ${pedido.nomeAlimento}: ${pedido.imagemAlimento}")

                                if (position != -1) {
                                    CardPedido(
                                        alimento = pedido.nomeAlimento,
                                        imagem = pedido.imagemAlimento,
                                        quantidade = pedido.quantidadePedido.toString(),
                                        empresa = pedido.nomeEmpresa,
                                        validade = pedido.dataValidade,
                                        onDelete = {
                                            pedidoParaExcluir.value = pedido
                                            showDeleteDialog.value = true
                                        },
                                        onClick = {
                                            // Navega para a tela de detalhes do alimento
                                            navegacao?.navigate("alimento/${pedido.idAlimento}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    // Diálogo de confirmação de exclusão
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog.value = false
                pedidoParaExcluir.value = null
            },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir este pedido?") },
            confirmButton = {
                val context = LocalContext.current
                Button(
                    onClick = {
                        val pedido = pedidoParaExcluir.value
                        if (pedido != null) {
                            val position = pedidosState?.indexOfFirst { it.idPedido == pedido.idPedido } ?: -1
                            if (position != -1) {
                                deletarPedido(
                                    idPedido = pedido.idPedido,
                                    position = position,
                                    sharedViewModel = sharedViewModel,
                                    context = context,
                                    onSuccess = {
                                        showDeleteDialog.value = false
                                        pedidoParaExcluir.value = null
                                    },
                                    onError = { errorMsg ->
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        showDeleteDialog.value = false
                                        pedidoParaExcluir.value = null
                                    }
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erro: Pedido não encontrado na lista",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showDeleteDialog.value = false
                                pedidoParaExcluir.value = null
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Erro: ID do pedido não encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                            showDeleteDialog.value = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        pedidoParaExcluir.value = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}