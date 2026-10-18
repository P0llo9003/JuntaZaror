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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                val nav = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val nombreUsuario by loginViewModel.nombreUsuario.collectAsState()

                NavHost(navController = nav, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onIrARegistro = { nav.navigate("registro") },
                            onLoginExitoso = { rol ->
                                val destino = if (rol == Rol.VECINO) "home" else "usuarios"
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
                        HomeScreen(nombre = nombreUsuario)
                    }

                    composable("usuarios") {
                        UsuariosScreen()
                    }
                }
            }
        }
    }
}