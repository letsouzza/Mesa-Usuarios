package br.senai.sp.jandira.mesausers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.senai.sp.jandira.mesausers.screens.CadastroOngs
import br.senai.sp.jandira.mesausers.screens.CadastroUser
import br.senai.sp.jandira.mesausers.screens.EscolhaCadastro
import br.senai.sp.jandira.mesausers.screens.LoginScreen
import br.senai.sp.jandira.mesausers.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navegacao = rememberNavController()
            NavHost(
                navController = navegacao,
                startDestination = "splash"
            ){
                composable(route = "login"){ LoginScreen(navegacao) }
                composable(route = "cadastroUser"){ CadastroUser(navegacao) }
                composable(route = "cadastroOngs"){ CadastroOngs(navegacao) }
                composable(route = "escolherCadastro"){ EscolhaCadastro(navegacao) }
                composable(route = "splash"){ SplashScreen(navegacao) }
            }
        }
    }
}

