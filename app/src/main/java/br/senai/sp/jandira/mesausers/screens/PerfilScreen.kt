package br.senai.sp.jandira.mesausers.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.*
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.ModalEdicaoPerfil
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.*
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import br.senai.sp.jandira.mesausers.MainActivity
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navegacao: NavHostController?,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    // Estados para dados do usuário
    var perfilUsuario by remember { mutableStateOf<UserCadastro?>(null) }
    var perfilOng by remember { mutableStateOf<OngsCadastro?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Dados do usuário logado
    val userId = sharedPreferences.getInt("user_id", 0)
    val userTipo = sharedPreferences.getString("user_tipo", "") ?: ""
    
    // Estado para controlar a exibição do modal
    var mostrarModalEdicao by remember { mutableStateOf(false) }
    
    // Estado para controlar se há modificações pendentes
    var temModificacoesPendentes by remember { mutableStateOf(false) }
    
    // Função para buscar dados da API (fallback)
    fun buscarDadosAPI() {
        when (userTipo.lowercase()) {
            "pessoa" -> {
                RetrofitFactory().getUserService().usuarioPorId(userId).enqueue(object : Callback<UserCadastro> {
                    override fun onResponse(call: Call<UserCadastro>, response: Response<UserCadastro>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            response.body()?.let { usuario ->
                                perfilUsuario = usuario
                                Log.d("PerfilScreen", "Dados do usuário carregados da API: ${usuario.nome}")
                            } ?: run {
                                errorMessage = "Dados do usuário não encontrados"
                            }
                        } else {
                            errorMessage = "Erro ao carregar dados do usuário"
                            Log.e("PerfilScreen", "Erro na resposta: ${response.code()}")
                        }
                    }
                    
                    override fun onFailure(call: Call<UserCadastro>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Falha na conexão"
                        Log.e("PerfilScreen", "Erro na requisição", t)
                    }
                })
            }
            "ong" -> {
                RetrofitFactory().getUserService().ongPorId(userId).enqueue(object : Callback<OngResponse> {
                    override fun onResponse(call: Call<OngResponse>, response: Response<OngResponse>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            response.body()?.let { ongResponse ->
                                perfilOng = OngsCadastro(
                                    id = ongResponse.id,
                                    nome = ongResponse.nome,
                                    email = ongResponse.email,
                                    telefone = ongResponse.telefone,
                                    foto = ongResponse.foto
                                )
                                Log.d("PerfilScreen", "Dados da ONG carregados da API: ${ongResponse.nome}")
                            } ?: run {
                                errorMessage = "Dados da ONG não encontrados"
                            }
                        } else {
                            errorMessage = "Erro ao carregar dados da ONG"
                            Log.e("PerfilScreen", "Erro na resposta: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<OngResponse>, t: Throwable) {
                        isLoading = false
                        errorMessage = "Falha na conexão"
                        Log.e("PerfilScreen", "Erro na requisição", t)
                    }
                })
            }
            else -> {
                errorMessage = "Tipo de usuário inválido"
                isLoading = false
            }
        }
    }
    
    // Função para carregar dados do usuário
    fun carregarDadosUsuario() {
        if (userId == 0 || userTipo.isEmpty()) {
            errorMessage = "Usuário não logado"
            isLoading = false
            return
        }
        
        isLoading = true
        errorMessage = null
        
        Log.d("PerfilScreen", "Carregando dados do usuário - ID: $userId, Tipo: $userTipo")
        
        // Primeiro, tentar carregar dados do SharedPreferences
        val dataComplete = sharedPreferences.getBoolean("user_data_complete", false)
        
        if (dataComplete) {
            // Carregar dados do SharedPreferences
            Log.d("PerfilScreen", "Carregando dados do SharedPreferences")
            
            when (userTipo.lowercase()) {
                "pessoa" -> {
                    perfilUsuario = UserCadastro(
                        id = sharedPreferences.getInt("user_id", 0),
                        nome = sharedPreferences.getString("user_nome", "") ?: "",
                        email = sharedPreferences.getString("user_email", "") ?: "",
                        cpf = sharedPreferences.getString("user_cpf", "") ?: "",
                        telefone = sharedPreferences.getString("user_telefone", "") ?: "",
                        foto = sharedPreferences.getString("user_foto", "") ?: ""
                    )
                    Log.d("PerfilScreen", "Dados do usuário carregados do SharedPreferences: ${perfilUsuario?.nome}")
                }
                "ong" -> {
                    perfilOng = OngsCadastro(
                        id = sharedPreferences.getInt("user_id", 0),
                        nome = sharedPreferences.getString("user_nome", "") ?: "",
                        email = sharedPreferences.getString("user_email", "") ?: "",
                        telefone = sharedPreferences.getString("user_telefone", "") ?: "",
                        foto = sharedPreferences.getString("user_foto", "") ?: ""
                    )
                    Log.d("PerfilScreen", "Dados da ONG carregados do SharedPreferences: ${perfilOng?.nome}")
                }
            }
            isLoading = false
        } else {
            // Fallback: buscar dados da API
            Log.d("PerfilScreen", "Dados não encontrados no SharedPreferences, buscando da API")
            buscarDadosAPI()
        }
    }
    
    // Função para limpar dados do usuário (logout)
    fun limparDadosUsuario() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        Log.d("PerfilScreen", "Dados do usuário limpos do SharedPreferences")
    }
    
    // Efeito para carregar os dados do usuário quando a tela for exibida
    LaunchedEffect(Unit) {
        Log.d("PerfilScreen", "Iniciando carregamento do perfil...")
        Log.d("PerfilScreen", "User ID: $userId, Tipo: $userTipo")
        carregarDadosUsuario()
    }
    
    // Efeito para observar mudanças no tipo de usuário
    LaunchedEffect(userTipo) {
        if (userTipo.isNotEmpty()) {
            Log.d("PerfilScreen", "Tipo de usuário alterado: $userTipo")
            carregarDadosUsuario()
        }
    }
    
    // Launcher para abrir a galeria
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            perfilUsuario?.let { perfil ->
                perfilUsuario = perfil.copy(foto = it.toString())
                temModificacoesPendentes = true
            }
        }
    }

    Scaffold(
        topBar = {
            BarraDeTitulo()
        },
        bottomBar = {
            BarraInferior(navegacao)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundLight)
        ) {
            // Estados de loading e erro
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage ?: "Erro desconhecido",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = poppinsFamily,
                                color = primaryLight,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { carregarDadosUsuario() },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryLight)
                            ) {
                                Text("Tentar novamente", color = Color.White)
                            }
                        }
                    }
                }
                perfilUsuario != null || perfilOng != null -> {
                    // Botão Salvar Modificações (quando há modificações pendentes)
                    if (temModificacoesPendentes) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // Salvar modificações no SharedPreferences
                            perfilUsuario?.let { usuario ->
                                val editor = sharedPreferences.edit()
                                editor.putString("user_nome", usuario.nome)
                                editor.putString("user_email", usuario.email)
                                editor.putString("user_cpf", usuario.cpf)
                                editor.putString("user_telefone", usuario.telefone)
                                editor.putString("user_foto", usuario.foto)
                                editor.apply()
                                
                                Log.d("PerfilScreen", "Dados atualizados no SharedPreferences")
                            }
                            temModificacoesPendentes = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Salvar",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.salvar_modificacoes),
                                color = Color.White,
                                fontFamily = poppinsFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Conteúdo principal com scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
            // Seção de saudação e foto do usuário
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto do perfil com opção de editar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(primaryLight.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val fotoUrl = perfilUsuario?.foto ?: perfilOng?.foto ?: ""
                        if (fotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = fotoUrl,
                                contentDescription = "Foto do perfil",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Imagem padrão quando não há foto
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Foto padrão",
                                modifier = Modifier.size(40.dp),
                                tint = primaryLight.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Texto "Editar" clicável
                    Text(
                        text = stringResource(R.string.editar),
                        fontSize = 14.sp,
                        fontFamily = poppinsFamily,
                        color = primaryLight,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            launcherGaleria.launch("image/*")
                        }
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Saudação e nome
                Column {
                    Text(
                        text = stringResource(R.string.ola),
                        fontSize = 24.sp,
                        fontFamily = poppinsFamily,
                        color = Color(0xFF000000)
                    )
                    Text(
                        text = perfilUsuario?.nome ?: perfilOng?.nome ?: "Nome não disponível",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                }
            }
            
            // Card Dados da conta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Título da seção com ícone de editar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.dados_conta),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = poppinsFamily,
                            color = primaryLight
                        )
                        
                        IconButton(
                            onClick = { mostrarModalEdicao = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar perfil",
                                tint = primaryLight,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Nome Completo
                    Text(
                        text = perfilUsuario?.nome ?: perfilOng?.nome ?: "Nome não disponível",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                    
                    // Email
                    Text(
                        text = "Email: " + (perfilUsuario?.email ?: perfilOng?.email ?: "Não informado"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                    
                    // CPF (apenas para usuário pessoa)
                    perfilUsuario?.let { usuario ->
                        Text(
                            text = "CPF: " + (usuario.cpf.ifEmpty { "Não informado" }),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = poppinsFamily,
                            color = primaryLight
                        )
                    }

                    // Telefone
                    Text(
                        text = "Telefone: " + (perfilUsuario?.telefone ?: perfilOng?.telefone ?: "Não informado"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                    
                    // Tipo de usuário
                    Text(
                        text = "Tipo: " + if (perfilUsuario != null) "Pessoa Física" else "ONG",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                }
            }
            
            // Card Favoritos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.favoritos),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                    
                    IconButton(
                        onClick = {
                            navegacao?.navigate("favoritos")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Ir para favoritos",
                            tint = primaryLight
                        )
                    }
                }
            }
            
            // Card Atualizar Senha
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.atualizar_senha),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                    
                    IconButton(
                        onClick = { 
                            navegacao?.navigate("recuperacao")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Atualizar senha",
                            tint = primaryLight
                        )
                    }
                }
            }
                    } // Fechamento da Column interna (conteúdo com scroll)
                }
            }
        }
        
        // Modal de edição de perfil (apenas se houver dados carregados)
        if (perfilUsuario != null) {
            ModalEdicaoPerfil(
                perfilUsuario = perfilUsuario!!,
                mostrarModal = mostrarModalEdicao,
                onDismiss = { mostrarModalEdicao = false },
                onAtualizar = { perfilAtualizado ->
                    perfilUsuario = perfilAtualizado
                    temModificacoesPendentes = true
                }
            )
        }
    }
}

//@Preview
//@Composable
//private fun PerfilScreenPreview() {
//    MesaTheme {
//        PerfilScreen(null)
//    }
//}
