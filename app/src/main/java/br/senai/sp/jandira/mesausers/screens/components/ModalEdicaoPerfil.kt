package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.model.UserCadastro
import br.senai.sp.jandira.mesausers.ui.theme.*

@Composable
fun ModalEdicaoPerfil(
    perfilUsuario: UserCadastro,
    mostrarModal: Boolean,
    onDismiss: () -> Unit,
    onAtualizar: (UserCadastro) -> Unit
) {
    if (mostrarModal) {
        var nomeCompleto by remember { mutableStateOf(perfilUsuario.nome) }
        var email by remember { mutableStateOf(perfilUsuario.email) }
        var cpf by remember { mutableStateOf(perfilUsuario.cpf) }
        var telefone by remember { mutableStateOf(perfilUsuario.telefone) }

        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título do modal
                    Text(
                        text = "Editar Perfil",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsFamily,
                        color = primaryLight,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Campo Nome Completo
                    OutlinedTextField(
                        value = nomeCompleto,
                        onValueChange = { nomeCompleto = it },
                        label = {
                            Text(
                                text = stringResource(R.string.nome_completo),
                                fontFamily = poppinsFamily,
                                color = primaryLight.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = primaryLight.copy(alpha = 0.5f),
                            focusedBorderColor = primaryLight,
                            focusedTextColor = primaryLight,
                            unfocusedTextColor = primaryLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Campo Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                text = stringResource(R.string.email),
                                fontFamily = poppinsFamily,
                                color = primaryLight.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = primaryLight.copy(alpha = 0.5f),
                            focusedBorderColor = primaryLight,
                            focusedTextColor = primaryLight,
                            unfocusedTextColor = primaryLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Campo CPF
                    OutlinedTextField(
                        value = cpf,
                        onValueChange = { cpf = it },
                        label = {
                            Text(
                                text = stringResource(R.string.cpf),
                                fontFamily = poppinsFamily,
                                color = primaryLight.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = primaryLight.copy(alpha = 0.5f),
                            focusedBorderColor = primaryLight,
                            focusedTextColor = primaryLight,
                            unfocusedTextColor = primaryLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Campo Telefone
                    OutlinedTextField(
                        value = telefone,
                        onValueChange = { telefone = it },
                        label = {
                            Text(
                                text = stringResource(R.string.telefone),
                                fontFamily = poppinsFamily,
                                color = primaryLight.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = primaryLight.copy(alpha = 0.5f),
                            focusedBorderColor = primaryLight,
                            focusedTextColor = primaryLight,
                            unfocusedTextColor = primaryLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Botões
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botão Cancelar
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = primaryLight
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Cancelar",
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Botão Atualizar
                        Button(
                            onClick = {
                                val perfilAtualizado = perfilUsuario.copy(
                                    nome = nomeCompleto,
                                    email = email,
                                    cpf = cpf,
                                    telefone = telefone
                                )
                                onAtualizar(perfilAtualizado)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryLight
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.atualizar),
                                color = Color.White,
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ModalEdicaoPerfilPreview() {
    MesaTheme {
        ModalEdicaoPerfil(
            perfilUsuario = UserCadastro(
                nome = "Mario James",
                email = "mario@gmail.com",
                cpf = "009.009.009-09",
                telefone = "(11) 94444-8888",
                foto = ""
            ),
            mostrarModal = true,
            onDismiss = {},
            onAtualizar = {}
        )
    }
}
