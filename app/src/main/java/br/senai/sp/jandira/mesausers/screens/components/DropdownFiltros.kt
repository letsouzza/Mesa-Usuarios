package br.senai.sp.jandira.mesausers.screens.components

import android.os.Build
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// Função para converter data de DD/MM/AAAA para formato da API (DD-MM-YYYY)
fun converterDataParaAPI(dataFormatada: String): String {
    try {
        // Formato atual: DD/MM/AAAA (ex: 20/09/2026)
        val partes = dataFormatada.split("/")
        if (partes.size == 3) {
            val dia = partes[0].padStart(2, '0')  // Garantir 2 dígitos
            val mes = partes[1].padStart(2, '0')  // Garantir 2 dígitos
            val ano = partes[2]

            // Formato DD-MM-YYYY conforme aceito pela stored procedure
            val formatoDDMMAAAA = "$dia-$mes-$ano"
            Log.d("DropdownFiltros", "Formato DD-MM-YYYY: '$dataFormatada' -> '$formatoDDMMAAAA'")
            return formatoDDMMAAAA
        }
    } catch (e: Exception) {
        Log.e("DropdownFiltros", "Erro ao converter data: $dataFormatada", e)
    }

    // Fallback: apenas trocar / por -
    val dataConvertida = dataFormatada.replace("/", "-")
    Log.d("DropdownFiltros", "Fallback - Convertendo data: '$dataFormatada' -> '$dataConvertida'")
    return dataConvertida
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFiltros(
    modifier: Modifier = Modifier,
    onEmpresaSelecionada: (Int) -> Unit = {},
    onDataSelecionada: (String) -> Unit = {}
) {
    val showDropdown = remember { mutableStateOf(false) }
    val empresaList = remember { mutableStateOf(listOf<Empresa>()) }
    val empresaSelecionada = remember { mutableStateOf<Empresa?>(null) }

    // Estados para o calendário
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dataSelecionada = remember { mutableStateOf<String?>(null) }

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
                        onEmpresaSelecionada(empresa.id)
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

            // Botão para selecionar data
            DropdownMenuItem(
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        // Botão para abrir calendário
                        OutlinedButton(
                            onClick = { showDatePicker.value = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dataSelecionada.value ?: "Selecionar Data",
                                fontFamily = poppinsFamily,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Botão filtrar
                        Button(
                            onClick = {
                                dataSelecionada.value?.let { data ->
                                    Log.d("DropdownFiltros", "Botão filtrar clicado!")
                                    Log.d("DropdownFiltros", "Data selecionada: '$data'")
                                    onDataSelecionada(data)
                                    showDropdown.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = secondaryLight,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = dataSelecionada.value != null
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

        // DatePicker Dialog
        if (showDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                // Formato DD-MM-YYYY conforme aceito pela stored procedure
                                val dataFormatada = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                dataSelecionada.value = dataFormatada
                                Log.d("DropdownFiltros", "Data selecionada no calendário (formato DD-MM-YYYY): $dataFormatada")
                            }
                            showDatePicker.value = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker.value = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    title = {
                        Text(
                            text = "Selecionar Data",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun DropdownFiltrosPreview() {
    DropdownFiltros()
}