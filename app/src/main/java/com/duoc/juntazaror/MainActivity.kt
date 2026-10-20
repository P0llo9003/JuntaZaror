package com.duoc.juntazaror

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.juntazaror.ui.screens.HomeScreen
import com.duoc.juntazaror.ui.screens.LoginScreen
import com.duoc.juntazaror.ui.screens.RegistroScreen
import com.duoc.juntazaror.ui.screens.UsuariosScreen
import com.duoc.juntazaror.ui.theme.AppTheme
import com.duoc.juntazaror.viewmodel.LoginViewModel
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.ui.screens.SolicitarArriendoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                val nav = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val nombreUsuario by loginViewModel.nombreUsuario.collectAsState()
                val usuario by loginViewModel.usuario.collectAsState()

                NavHost(navController = nav, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onIrARegistro = { nav.navigate("registro") },
                            onLoginExitoso = { rol ->
                                val destino = "home"
                                nav.navigate(destino) {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("registro") {
                        RegistroScreen(onVolverALogin = { nav.popBackStack() })
                    }

                    composable("home") {
                        HomeScreen(
                            usuario = usuario,
                            onSolicitarArriendo = { nav.navigate("solicitar_arriendo") },
                            onRegistrarMovimiento = { nav.navigate("registrar_movimiento") }
                        )
                    }

                    composable("usuarios") {
                        UsuariosScreen()
                    }

                    composable("solicitar_arriendo") {
                        usuario?.let {
                            SolicitarArriendoScreen(
                                usuario = it,
                                onVolver = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}