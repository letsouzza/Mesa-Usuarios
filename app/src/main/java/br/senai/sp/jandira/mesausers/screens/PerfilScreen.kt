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
import br.senai.sp.jandira.mesausers.model.UserCadastro
import br.senai.sp.jandira.mesausers.screens.components.BarraInferior
import br.senai.sp.jandira.mesausers.screens.components.BarraDeTitulo
import br.senai.sp.jandira.mesausers.screens.components.ModalEdicaoPerfil
import br.senai.sp.jandira.mesausers.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    controleNavegacao: NavHostController?
) {
    // Dados falsos do usuário (posteriormente virão da API)
    var perfilUsuario by remember {
        mutableStateOf(
            UserCadastro(
                nome = "Mario James",
                email = "mario@gmail.com",
                cpf = "009.009.009-09",
                telefone = "(11) 94444-8888",
                foto = ""
            )
        )
    }
    
    // Estado para controlar a exibição do modal
    var mostrarModalEdicao by remember { mutableStateOf(false) }
    
    // Estado para controlar se há modificações pendentes
    var temModificacoesPendentes by remember { mutableStateOf(false) }
    
    // Launcher para abrir a galeria
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            perfilUsuario = perfilUsuario.copy(foto = it.toString())
            temModificacoesPendentes = true
        }
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
        ) {
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
                            // Aqui você pode implementar a lógica para salvar na API
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
                        if (perfilUsuario.foto.isNotEmpty()) {
                            AsyncImage(
                                model = perfilUsuario.foto,
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
                        text = perfilUsuario.nome,
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
                        text = perfilUsuario.nome,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                    
                    // Email
                    Text(
                        text = stringResource(R.string.email) + perfilUsuario.email,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                    
                    // CPF
                    Text(
                        text = stringResource(R.string.cpf) + perfilUsuario.cpf,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )

                    // Telefone
                    Text(
                        text = stringResource(R.string.telefone) + perfilUsuario.telefone,
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
                            controleNavegacao?.navigate("favoritos")
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
                            controleNavegacao?.navigate("recuperacao")
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
        
        // Modal de edição de perfil
        ModalEdicaoPerfil(
            perfilUsuario = perfilUsuario,
            mostrarModal = mostrarModalEdicao,
            onDismiss = { mostrarModalEdicao = false },
            onAtualizar = { perfilAtualizado ->
                perfilUsuario = perfilAtualizado
                temModificacoesPendentes = true
            }
        )
    }
}

@Preview
@Composable
private fun PerfilScreenPreview() {
    MesaTheme {
        PerfilScreen(null)
    }
}
