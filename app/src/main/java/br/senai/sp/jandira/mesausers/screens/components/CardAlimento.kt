package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import br.senai.sp.jandira.mesausers.ui.theme.primaryLight
import coil.compose.AsyncImage

@Composable
fun CardAlimento(
    id: Int = 0,
    img: String = "https://mesaplustcc.blob.core.windows.net/fotos/106aab1d-a736-429f-9b8a-af40d61562d3.jpg",
    nome: String = "Feijão",
    prazo: String = "25/09/25",
    quantidade: String = "5",
    imgEmpresa: String = "",
    empresa: String = "Atacadão",
    onClick: (Int) -> Unit,
    navController: NavHostController? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clickable {
                if (id > 0) {
                    navController?.navigate("alimento/$id")
                } else {
                    onClick?.invoke(id)
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = primaryLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Imagem do produto
            Card(
                modifier = Modifier
                    .height(140.dp)
                    .width(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                AsyncImage(
                    model = img,
                    contentDescription = "Imagem do $nome",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Informações do produto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Nome do produto
                Text(
                    text = nome,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppinsFamily
                )

                // Prazo de validade
                Text(
                    text = stringResource(R.string.prazo_curto) + prazo,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = poppinsFamily
                )

                // Quantidade
                Text(
                    text = stringResource(R.string.quantidade) + quantidade,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = poppinsFamily
                )

                // Empresa
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícone da empresa (círculo laranja com "A")
                    Card(
                        modifier = Modifier
                            .size(24.dp),
                        shape = CircleShape,
                    ) {
                        AsyncImage(
                            model = imgEmpresa,
                            contentDescription = "Imagem do ${empresa}",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = empresa,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = poppinsFamily
                    )
                }
            }

            // Ícone do carrinho
            IconButton(
                onClick = { onClick(id) }
            ){
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = "Adicionar ao carrinho",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun CardAlimentoPreview() {
    CardAlimento(onClick = {})
}