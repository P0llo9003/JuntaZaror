package com.duoc.juntazaror

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.juntazaror.ui.screens.LoginScreen
import com.duoc.juntazaror.ui.screens.RegistroScreen
import com.duoc.juntazaror.ui.screens.UsuariosScreen
import com.duoc.juntazaror.ui.theme.AppTheme
import com.duoc.juntazaror.viewmodel.LoginViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                val nav = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()

                NavHost(navController = nav, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onIrARegistro = { nav.navigate("registro") },
                            onLoginExitoso = { nav.navigate("usuarios") }
                        )
                    }

                    composable("registro") {
                        RegistroScreen(
                            onVolverALogin = { nav.popBackStack() }
                        )
                    }

                    composable("usuarios") {
                        UsuariosScreen(
                        )
                    }


                }
            }
        }
    }
}