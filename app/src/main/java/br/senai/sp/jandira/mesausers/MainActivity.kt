package br.senai.sp.jandira.mesausers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.senai.sp.jandira.mesausers.model.SharedViewModel
import br.senai.sp.jandira.mesausers.screens.AtualizacaoSenha
import br.senai.sp.jandira.mesausers.screens.CadastroOngs
import br.senai.sp.jandira.mesausers.screens.CadastroUser
import br.senai.sp.jandira.mesausers.screens.CodigoSenha
import br.senai.sp.jandira.mesausers.screens.DetalhesScreen
import br.senai.sp.jandira.mesausers.screens.EscolhaCadastro
import br.senai.sp.jandira.mesausers.screens.FavoritosScreen
import br.senai.sp.jandira.mesausers.screens.HomeScreen
import br.senai.sp.jandira.mesausers.screens.InstituicaoScreen
import br.senai.sp.jandira.mesausers.screens.LoginScreen
import br.senai.sp.jandira.mesausers.screens.PedidosScreen
import br.senai.sp.jandira.mesausers.screens.PerfilScreen
import br.senai.sp.jandira.mesausers.screens.RecuperacaoSenha
import br.senai.sp.jandira.mesausers.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navegacao = rememberNavController()
            val sharedViewModel: SharedViewModel = viewModel()

            NavHost(
                navController = navegacao,
                startDestination = "splash"
            ){
                composable(route = "login"){ LoginScreen(navegacao, sharedViewModel) }
                composable(route = "cadastroUser"){ CadastroUser(navegacao) }
                composable(route = "cadastroOngs"){ CadastroOngs(navegacao) }
                composable(route = "escolherCadastro"){ EscolhaCadastro(navegacao) }
                composable(route = "splash"){ SplashScreen(navegacao) }
                composable(route = "recuperacao"){ RecuperacaoSenha(navegacao) }
                composable(route = "codigo"){ CodigoSenha(navegacao) }
                composable(route = "atualizarSenha"){ AtualizacaoSenha(navegacao) }
                composable(route = "home"){ HomeScreen(navegacao, sharedViewModel) }
                composable(
                    route = "alimento/{alimentoId}",
                    arguments = listOf(navArgument("alimentoId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val alimentoId = backStackEntry.arguments?.getInt("alimentoId") ?: 0
                    DetalhesScreen(navegacao, alimentoId, sharedViewModel)
                }
                composable(route = "perfil"){ PerfilScreen(navegacao) }
                composable(route = "pedidos"){ PedidosScreen(navegacao, sharedViewModel) }
                composable(route = "favoritos"){ FavoritosScreen(navegacao) }
                composable(
                    route = "instituicao/{empresaId}",
                    arguments = listOf(navArgument("empresaId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val empresaId = backStackEntry.arguments?.getInt("empresaId") ?: 0
                    InstituicaoScreen(navegacao, empresaId, sharedViewModel)
                }
            }
        }
    }
}