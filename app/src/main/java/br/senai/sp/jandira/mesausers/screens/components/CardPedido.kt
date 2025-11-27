package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.Locale

// Função para formatar a data de yyyy-MM-dd para dd/MM/yyyy
fun formatarDataValidade(dataString: String?): String {
    if (dataString == null) return "Não informada"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dataString)
        date?.let { outputFormat.format(it) } ?: dataString
    } catch (e: Exception) {
        dataString // Retorna a string original se houver erro
    }
}

@Composable
fun CardPedido(
    alimento: String,
    imagem: String?,
    quantidade: String,
    empresa: String,
    validade: String?,
    onDelete: () -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4B734F))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(100.dp, 120.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                // Verifica se a imagem não está vazia e constrói a URL corretamente
                if (!imagem.isNullOrEmpty()) {
                    val imageUrl = when {
                        imagem.startsWith("http") -> imagem
                        imagem.startsWith("//") -> "https:$imagem"
                        else -> "https://$imagem"
                    }
                    
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagem do $alimento",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Exibe um placeholder quando não há imagem
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sem imagem",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = alimento,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppinsFamily,
                    color = Color(0xFFFFF9EB)
                )
                Text(
                    text = "Instituição: $empresa",
                    fontSize = 14.sp,
                    fontFamily = poppinsFamily,
                    color = Color(0xFFFFF9EB)
                )
                Text(
                    text = "Validade: ${formatarDataValidade(validade)}",
                    fontSize = 14.sp,
                    fontFamily = poppinsFamily,
                    color = Color(0xFFFFF9EB)
                )
                Text(
                    text = "Quantidade: $quantidade",
                    fontSize = 14.sp,
                    fontFamily = poppinsFamily,
                    color = Color(0xFFFFF9EB)
                )
            }
            Column(
                modifier = Modifier.height(120.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir Pedido",
                        tint = Color(0xFFFFF9EB),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF9EB)
@Composable
private fun CardPedidoPreview() {
//    CardPedido(
//        nomeAlimento = "Pacote de Macarrão",
//        imagemAlimento = "",
//        quantidade = "5",
//        nomeEmpresa = "Assai Barueri",
//        validade = "2025-10-13T00:00:00.000Z",
//        onDelete = {}
//    )
}