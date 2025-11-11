package br.senai.sp.jandira.mesausers.screens.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.Empresa
import br.senai.sp.jandira.mesausers.model.ListEmpresa
import br.senai.sp.jandira.mesausers.service.RetrofitFactory
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily
import br.senai.sp.jandira.mesausers.ui.theme.primaryLight
import br.senai.sp.jandira.mesausers.ui.theme.secondaryLight
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Função para formatar data com controle de cursor
fun formatarDataComCursor(currentValue: TextFieldValue, newInput: String): TextFieldValue {
    // Se o usuário está apagando, permite apagar normalmente
    if (newInput.length < currentValue.text.length) {
        return TextFieldValue(
            text = newInput,
            selection = TextRange(newInput.length)
        )
    }

    // Remove tudo que não é dígito
    val digitsOnly = newInput.filter { it.isDigit() }

    // Limita a 8 dígitos
    val limitedDigits = if (digitsOnly.length > 8) digitsOnly.substring(0, 8) else digitsOnly

    val formattedText = when (limitedDigits.length) {
        0 -> ""
        1 -> limitedDigits
        2 -> limitedDigits
        3 -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2)}"
        4 -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2)}"
        5 -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2, 4)}/${limitedDigits.substring(4)}"
        6 -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2, 4)}/${limitedDigits.substring(4)}"
        7 -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2, 4)}/${limitedDigits.substring(4)}"
        8 -> "${limitedDigits.substring(0, 2)}/${limitedDigits.substring(2, 4)}/${limitedDigits.substring(4)}"
        else -> limitedDigits
    }

    // Posiciona o cursor no final do texto
    return TextFieldValue(
        text = formattedText,
        selection = TextRange(formattedText.length)
    )
}

@Composable
fun DropdownFiltros(
    modifier: Modifier = Modifier
) {
    val showDropdown = remember { mutableStateOf(false) }
    val empresaList = remember { mutableStateOf(listOf<Empresa>()) }
    val empresaSelecionada = remember { mutableStateOf<Empresa?>(null) }
    val filtroTexto = remember { mutableStateOf(TextFieldValue("")) }

    // Carregar empresas da API
    LaunchedEffect(Unit) {
        val callEmpresa = RetrofitFactory()
            .getUserService()
            .listEmpresa()

        callEmpresa.enqueue(object : Callback<ListEmpresa> {
            override fun onResponse(call: Call<ListEmpresa>, response: Response<ListEmpresa>) {
                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        empresaList.value = result.empresas
                        Log.d("DropdownFiltros", "Empresas carregadas: ${result.empresas.size}")
                    }
                } else {
                    Log.e("DropdownFiltros", "Erro ao carregar empresas: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ListEmpresa>, t: Throwable) {
                Log.e("DropdownFiltros", "Erro na requisição de empresas", t)
            }
        })
    }

    Box(modifier = modifier) {
        Button(
            onClick = { showDropdown.value = !showDropdown.value },
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryLight,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.filtros),
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color.Black
            )
        }

        DropdownMenu(
            expanded = showDropdown.value,
            onDismissRequest = { showDropdown.value = false },
            modifier = Modifier
                .width(280.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            // Título Filtrar por Empresa
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.filtrar_por_empresa),
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = primaryLight
                    )
                },
                onClick = { }
            )

            // Lista de empresas com design melhorado
            empresaList.value.take(5).forEachIndexed { index, empresa ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ícone da empresa
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(secondaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = empresa.nome,
                                fontFamily = poppinsFamily,
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = if (empresaSelecionada.value?.id == empresa.id)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    onClick = {
                        empresaSelecionada.value = empresa
                        showDropdown.value = false
                    }
                )

                // Divisória entre empresas
                if (index < empresaList.value.take(5).size - 1) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray.copy(alpha = 0.3f),
                        thickness = 0.5.dp
                    )
                }
            }

            // Espaçamento entre seções
            Spacer(modifier = Modifier.height(16.dp))

            // Título Filtrar por Data
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.filtrar_por_data),
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = primaryLight
                    )
                },
                onClick = { }
            )

            // Campo de data com formatação automática
            DropdownMenuItem(
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = filtroTexto.value,
                            onValueChange = { newValue ->
                                val formatted = formatarDataComCursor(filtroTexto.value, newValue.text)
                                if (formatted.text.length <= 10) { // Limita a 10 caracteres (DD/MM/AAAA)
                                    filtroTexto.value = formatted
                                }
                            },
                            placeholder = {
                                Text(
                                    "DD/MM/AAAA",
                                    fontFamily = poppinsFamily,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                showDropdown.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = secondaryLight,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.filtrar),
                                fontFamily = poppinsFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                },
                onClick = { }
            )
        }
    }
}

@Preview
@Composable
private fun DropdownFiltrosPreview() {
    DropdownFiltros()
}
