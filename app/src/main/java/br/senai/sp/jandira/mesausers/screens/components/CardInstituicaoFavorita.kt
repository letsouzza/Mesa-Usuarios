package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.ui.theme.*

// Estrutura de dados para instituições favoritas
data class InstituicaoFavorita(
    val id: String,
    val nome: String,
    val distancia: String,
    val logoUrl: String,
    val isFavorito: Boolean = true
)

@Composable
fun CardInstituicaoFavorita(
    instituicao: InstituicaoFavorita,
    onFavoritoClick: (String) -> Unit = {},
    onRemoverFavorito: (String) -> Unit = {}
) {
    var mostrarModalConfirmacao by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo da instituição
            AsyncImage(
                model = instituicao.logoUrl,
                contentDescription = instituicao.nome,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Informações da instituição
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nome da instituição
                Text(
                    text = instituicao.nome,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = poppinsFamily,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Distância
                Text(
                    text = "${stringResource(R.string.distancia)} ${instituicao.distancia}",
                    fontSize = 14.sp,
                    fontFamily = poppinsFamily,
                    color = Color.Gray
                )
            }
            
            // Ícone de coração
            IconButton(
                onClick = {
                    if (instituicao.isFavorito) {
                        // Se está favoritado, mostra modal de confirmação
                        mostrarModalConfirmacao = true
                    } else {
                        // Se não está favoritado, adiciona aos favoritos
                        onFavoritoClick(instituicao.id)
                    }
                }
            ) {
                Icon(
                    imageVector = if (instituicao.isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (instituicao.isFavorito) "Remover dos favoritos" else "Adicionar aos favoritos",
                    tint = if (instituicao.isFavorito) Color.Red else primaryLight,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // Divisor
        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
    
    // Modal de confirmação para remoção
    if (mostrarModalConfirmacao) {
        AlertDialog(
            onDismissRequest = { mostrarModalConfirmacao = false },
            title = {
                Text(
                    text = stringResource(R.string.remover_favorito),
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryLight
                )
            },
            text = {
                Text(
                    text = "Tem certeza que deseja remover \"${instituicao.nome}\" dos seus favoritos?",
                    fontFamily = poppinsFamily,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarModalConfirmacao = false
                        onRemoverFavorito(instituicao.id)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.ok),
                        fontFamily = poppinsFamily,
                        color = primaryLight
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarModalConfirmacao = false }
                ) {
                    Text(
                        text = stringResource(R.string.cancelar),
                        fontFamily = poppinsFamily,
                        color = Color.Gray
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview
@Composable
private fun CardInstituicaoFavoritaPreview() {
    MesaTheme {
        CardInstituicaoFavorita(
            instituicao = InstituicaoFavorita(
                id = "1",
                nome = "Assaí",
                distancia = "5KM",
                logoUrl = "https://via.placeholder.com/60x60/FF6B00/FFFFFF?text=Assaí",
                isFavorito = true
            )
        )
    }
}
