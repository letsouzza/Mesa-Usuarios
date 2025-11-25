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
import br.senai.sp.jandira.mesausers.model.ResponseCadastro
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
                        val currentList = sharedViewModel.pedidos.value?.toMutableList()
                        if (position in 0 until (currentList?.size ?: 0)) {
                            currentList?.removeAt(position)
                            sharedViewModel.updatePedidos(currentList)
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
                                val position = pedidosState.value?.indexOfFirst { it.idPedido == pedido.idPedido } ?: -1
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
                            val position = pedidosState.value?.indexOfFirst { it.idPedido == pedido.idPedido } ?: -1
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