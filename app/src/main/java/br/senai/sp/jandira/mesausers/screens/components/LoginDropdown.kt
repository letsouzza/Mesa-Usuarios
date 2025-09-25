package br.senai.sp.jandira.mesausers.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.mesausers.R
import br.senai.sp.jandira.mesausers.ui.theme.poppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginDropdown(
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            // Campo do dropdown
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedText,
                onValueChange = {},
                label = {
                    Text(
                        text = stringResource(R.string.escolha_login),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = poppinsFamily,
                        color = Color(0x99000000)
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(35.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    unfocusedContainerColor = Color(0xFFFFE6B1),
                    focusedContainerColor = Color(0xFFFFE6B1),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Menu dropdown
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFE6B1)) // Fundo do menu inteiro
            ) {

                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE6B1)),
                    text = {
                        Text(
                            text = stringResource(R.string.pessoa),
                            color = Color.Black,
                            fontFamily = poppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    },
                    onClick = {
                        selectedText = "pessoa"
                        expanded = false
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE6B1)),
                    text = {
                        Text(
                            text = stringResource(R.string.ongs),
                            color = Color(0xFF261C09),
                            fontFamily = poppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    },
                    onClick = {
                        selectedText = "ong"
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun DropDownItemPreview() {
    LoginDropdown()
}