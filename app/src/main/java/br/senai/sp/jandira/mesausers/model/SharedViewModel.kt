package br.senai.sp.jandira.mesausers.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var id_usuario: Int = 0
    var id_ong: Int = 0
    var tipo_usuario: String = ""
    
    private val _pedidos = MutableLiveData<List<PedidoUsuario>?>()
    val pedidos: LiveData<List<PedidoUsuario>?> = _pedidos
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    fun updatePedidos(newPedidos: List<PedidoUsuario>?) {
        _pedidos.value = newPedidos
    }
    
    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
}