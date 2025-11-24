package br.senai.sp.jandira.mesausers.model

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var id_usuario: Int = 0
    var id_ong: Int = 0
    var tipo_usuario: String = ""
}