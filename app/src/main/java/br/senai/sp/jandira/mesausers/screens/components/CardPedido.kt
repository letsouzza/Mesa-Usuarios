package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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

// Estrutura de dados para pedidos
data class Pedido(
    val id: String,
    val nomeProduto: String,
    val instituicao: String,
    val validade: String,
    val quantidade: Int,
    val distancia: String,
    val imagemProduto: String
)

@Composable
fun CardPedido(
    pedido: Pedido,
    onDeleteClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = primaryLight),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Imagem do produto
            AsyncImage(
                model = pedido.imagemProduto,
                contentDescription = pedido.nomeProduto,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Informações do produto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nome do produto
                Text(
                    text = "${stringResource(R.string.pacote_de)} ${pedido.nomeProduto}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = poppinsFamily,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Instituição
                Text(
                    text = "${stringResource(R.string.instituicao)} ${pedido.instituicao}",
                    fontSize = 12.sp,
                    fontFamily = poppinsFamily,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Validade
                Text(
                    text = "${stringResource(R.string.validade)} ${pedido.validade}",
                    fontSize = 12.sp,
                    fontFamily = poppinsFamily,
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Quantidade e distância
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.quantidade_pedido)} ${pedido.quantidade}",
                        fontSize = 12.sp,
                        fontFamily = poppinsFamily,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = pedido.distancia,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = poppinsFamily,
                        color = Color.White
                    )
                }
            }
            
            // Ícone de lixeira
            IconButton(
                onClick = { onDeleteClick(pedido.id) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover pedido",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun CardPedidoPreview() {
    MesaTheme {
        CardPedido(
            pedido = Pedido(
                id = "1",
                nomeProduto = "Macarrão",
                instituicao = "Assai Barueri",
                validade = "13/10/2025",
                quantidade = 5,
                distancia = "5KM",
                imagemProduto = "https://via.placeholder.com/60x60/8B4513/FFFFFF?text=Macarrão"
            )
        )
    }
}
