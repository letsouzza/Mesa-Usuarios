package br.senai.sp.jandira.mesausers.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.FavoritoUsuario
import br.senai.sp.jandira.mesausers.model.ListFavoritosResponse
import br.senai.sp.jandira.mesausers.model.ListPedidosResponse
import br.senai.sp.jandira.mesausers.model.PedidoUsuario
import br.senai.sp.jandira.mesausers.model.ResponseGeral
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.CardInstituicaoFavorita
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    controleNavegacao: NavHostController?,
    sharedViewModel: SharedViewModel
) {
    // Estado para controlar o diálogo de confirmação
    val showDeleteDialog = remember { mutableStateOf(false) }
    // Estado para armazenar o pedido a ser excluído
    val favoritoParaExcluir = remember { mutableStateOf<FavoritoUsuario?>(null) }

    val favoritoState by sharedViewModel.favoritos.observeAsState(initial = null)
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage by sharedViewModel.errorMessage.observeAsState(initial = null)

    LaunchedEffect(Unit) {
        if (sharedViewModel.favoritos.value == null || sharedViewModel.favoritos.value.isNullOrEmpty()) {
            // Busca os favoritos do usuário
            val call = RetrofitFactory().getFavoritoService().getFavoritos(
                mapOf("id_usuario" to sharedViewModel.id_usuario.toString())
            )

            call.enqueue(object : Callback<ListFavoritosResponse> {
                override fun onResponse(
                    call: Call<ListFavoritosResponse>,
                    response: Response<ListFavoritosResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.status == true) {
                            sharedViewModel.updateFavoritos(responseBody.favoritos)

                            val favoritos = responseBody?.favoritos
                            if (favoritos == null || favoritos.isEmpty()) {
                                sharedViewModel.setErrorMessage("Você adicionou nehuma empresa aos favoritos")
                            } else {
                                sharedViewModel.setErrorMessage(null)
                            }
                        } else {
                            sharedViewModel.setErrorMessage(
                                responseBody?.message ?: "Erro ao carregar empresas favoritas"
                            )
                        }
                    } else {
                        sharedViewModel.setErrorMessage("Erro de servidor: ${response.code()}")
                        Log.e(
                            "FavoritosScreen",
                            "Erro na resposta: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ListFavoritosResponse>, t: Throwable) {
                    isLoading.value = false
                    sharedViewModel.setErrorMessage("Falha na conexão. Verifique sua internet.")
                    Log.e("FavoritosScreen", "Falha na requisição", t)
                }
            })
        } else {
            isLoading.value = false
        }
    }

    fun deletarFavorito(
        idFavorito: Int,
        position: Int,
        sharedViewModel: SharedViewModel,
        context: android.content.Context,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val call = RetrofitFactory().getFavoritoService().deletarFavorito(idFavorito)

        call.enqueue(object : Callback<ResponseGeral> {
            override fun onResponse(call: Call<ResponseGeral>, response: Response<ResponseGeral>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == true) {
                        // Atualiza a lista local removendo o item excluído
                        val currentList =
                            sharedViewModel.favoritos.value?.toMutableList() ?: mutableListOf()
                        if (position in currentList.indices) {
                            currentList.removeAt(position)
                            sharedViewModel.updateFavoritos(currentList.toList())
                        }

                        Toast.makeText(
                            context,
                            responseBody.message ?: "Empresa removido dos favoritos!",
                            Toast.LENGTH_SHORT
                        ).show()

                        onSuccess()

                        if (currentList.isNullOrEmpty()) {
                            sharedViewModel.setErrorMessage("Você ainda não favoritou nenhuma empresa")
                        }
                    } else {
                        val errorMsg = responseBody?.message ?: "Erro ao remover empresa favorita"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        onError(errorMsg)
                    }
                } else {
                    val errorMsg = "Erro ao remover empresa favorita: ${response.code()}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ResponseGeral>, t: Throwable) {
                val errorMsg = "Falha na conexão. Verifique sua internet."
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                Log.e("FavoritosScreen", "Falha ao deletar pedido", t)
                onError(errorMsg)
            }
        })
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
                favoritoState!= null -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Título da tela
                            Text(
                                text = stringResource(R.string.instituicoes_favoritas),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = poppinsFamily,
                                color = primaryLight,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                        }
                        items(favoritoState!!) { favorito ->
                            val position =
                                favoritoState?.indexOfFirst { it.idFavorito == favorito.idFavorito }
                                    ?: -1
                            if (position != -1) {
                                CardInstituicaoFavorita(
                                    id = favorito.idEmpresa,
                                    nome = favorito.nomeEmpresa,
                                    telefone = favorito.telefone,
                                    logoUrl = favorito.imagemEmpresa,
                                    isFavorito = true,
                                    onRemoverFavorito = {
                                        favoritoParaExcluir.value = favorito
                                        showDeleteDialog.value = true
                                    },
                                    onClick = { empresaId ->
                                        controleNavegacao?.navigate("instituicao/$empresaId")
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                        }
                    }
                }
            }
        }
    }
    // Diálogo de confirmação de exclusão
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog.value = false
                favoritoParaExcluir.value = null
            },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja tirar essa empresa dos favoritos?") },
            confirmButton = {
                val context = LocalContext.current
                Button(
                    onClick = {
                        val favorito = favoritoParaExcluir.value
                        if (favorito != null) {
                            val position = favoritoState?.indexOfFirst { it.idFavorito == favorito.idFavorito } ?: -1
                            if (position != -1) {
                                deletarFavorito(
                                    idFavorito = favorito.idFavorito,
                                    position = position,
                                    sharedViewModel = sharedViewModel,
                                    context = context,
                                    onSuccess = {
                                        showDeleteDialog.value = false
                                        favoritoParaExcluir.value = null
                                    },
                                    onError = { errorMsg ->
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        showDeleteDialog.value = false
                                        favoritoParaExcluir.value = null
                                    }
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erro: Empresa favorita não encontrada na lista",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showDeleteDialog.value = false
                                favoritoParaExcluir.value = null
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Erro: ID da empresa não encontrado",
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
                        favoritoParaExcluir.value = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

//@Preview
//@Composable
//private fun FavoritosScreenPreview() {
//    MesaTheme {
//        FavoritosScreen(null, sharedViewModel)
//    }
//}
