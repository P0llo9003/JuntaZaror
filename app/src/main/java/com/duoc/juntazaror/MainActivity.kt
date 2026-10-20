package com.duoc.juntazaror

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.juntazaror.notificaciones.Notificaciones
import com.duoc.juntazaror.ui.screens.HomeScreen
import com.duoc.juntazaror.ui.screens.LoginScreen
import com.duoc.juntazaror.ui.screens.RegistrarMovimientoScreen
import com.duoc.juntazaror.ui.screens.RegistroScreen
import com.duoc.juntazaror.ui.screens.SolicitarArriendoScreen
import com.duoc.juntazaror.ui.screens.UsuariosScreen
import com.duoc.juntazaror.ui.theme.AppTheme
import com.duoc.juntazaror.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Notificaciones.crearCanales(this)

        setContent {
            AppTheme {
                val context = LocalContext.current
                val nav = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val usuario by loginViewModel.usuario.collectAsState()

                val permisoNotificaciones = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { }
                LaunchedEffect(Unit) {
                    val concedido = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!concedido) {
                        permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                NavHost(navController = nav, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onIrARegistro = { nav.navigate("registro") },
                            onLoginExitoso = {
                                nav.navigate("home") {
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
                            onRegistrarMovimiento = { nav.navigate("registrar_movimiento") },
                            onCerrarSesion = {
                                loginViewModel.cerrarSesion()
                                nav.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
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

                    composable("registrar_movimiento") {
                        RegistrarMovimientoScreen(onVolver = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}