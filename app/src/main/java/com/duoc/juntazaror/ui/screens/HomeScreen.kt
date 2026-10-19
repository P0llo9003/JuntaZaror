package com.duoc.juntazaror.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.ui.theme.AppTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.graphicsLayer
import com.duoc.juntazaror.R


@Composable
fun HomeScreen(
    usuario: UsuarioSesion?, onSolicitarArriendo: () -> Unit, onRegistrarMovimiento: () -> Unit) {
    if (usuario == null) return

    val destinos = remember(usuario.rol) { destinosPara(usuario.rol) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            destinos.forEach { destino ->
                item(
                    icon = { IconoAnimado(destino.iconRes, destino == currentDestination) },
                    label = { Text(destino.label) },
                    selected = destino == currentDestination,
                    onClick = { currentDestination = destino }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            when (currentDestination) {
                AppDestinations.HOME -> InicioContent(usuario)
                AppDestinations.ARRIENDOS -> ArriendosContent(usuario, onSolicitarArriendo)
                AppDestinations.SOLICITUDES -> SolicitudesScreen(usuario)
                AppDestinations.MOVIMIENTOS -> MovimientosScreen(usuario, onRegistrarMovimiento)
                AppDestinations.TRANSPARENCIA -> SeccionEnConstruccion("Transparencia")
                AppDestinations.PROFILE -> SeccionEnConstruccion("Perfil")
            }
        }
    }
}
@Composable
private fun InicioContent(usuario: UsuarioSesion) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text(
            text = if (usuario.nombre.isBlank()) "Bienvenido" else "Bienvenido, ${usuario.nombre}",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun SeccionEnConstruccion(titulo: String) {
    Column(modifier = Modifier.padding(20.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        Text("Próximamente")
    }
}
@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun IconoAnimado(@DrawableRes recurso: Int, selected: Boolean) {
    val vector = AnimatedImageVector.animatedVectorResource(recurso)
    Icon(
        painter = rememberAnimatedVectorPainter(vector, atEnd = selected),
        contentDescription = null
    )
}

@Composable
private fun IconoNav(emoji: String, selected: Boolean) {
    val escala by animateFloatAsState(
        targetValue = if (selected) 1.25f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "escala_icono_nav"
    )
    val opacidad by animateFloatAsState(
        targetValue = if (selected) 1f else 0.6f,
        animationSpec = tween(200),
        label = "opacidad_icono_nav"
    )

    Text(
        text = emoji,
        modifier = Modifier.graphicsLayer {
            scaleX = escala
            scaleY = escala
            alpha = opacidad
        }
    )
}

enum class AppDestinations(val label: String, @DrawableRes val iconRes: Int) {
    HOME("Inicio", R.drawable.ic_home_animado),
    ARRIENDOS("Arriendos", R.drawable.ic_arriendos_animado),
    SOLICITUDES("Solicitudes", R.drawable.ic_solicitudes_animado),
    MOVIMIENTOS("Movimientos", R.drawable.ic_movimientos_animado),
    TRANSPARENCIA("Transparencia", R.drawable.ic_transparencia_animado),
    PROFILE("Perfil", R.drawable.ic_perfil_animado)
}

fun destinosPara(rol: Rol): List<AppDestinations> = when (rol) {
    Rol.VECINO -> listOf(
        AppDestinations.HOME, AppDestinations.ARRIENDOS,
        AppDestinations.TRANSPARENCIA, AppDestinations.PROFILE
    )
    Rol.DIRECTIVA -> listOf(
        AppDestinations.HOME, AppDestinations.SOLICITUDES,
        AppDestinations.TRANSPARENCIA, AppDestinations.PROFILE
    )
    Rol.TESORERIA -> listOf(
        AppDestinations.HOME, AppDestinations.MOVIMIENTOS,
        AppDestinations.TRANSPARENCIA, AppDestinations.PROFILE
    )
}

@Composable
private fun ArriendosContent(usuario: UsuarioSesion, onSolicitarArriendo: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Arriendos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onSolicitarArriendo, modifier = Modifier.fillMaxWidth()) {
            Text("Solicitar arriendo")
        }
        Spacer(modifier = Modifier.height(20.dp))
        MisSolicitudesLista(usuario = usuario, modifier = Modifier.weight(1f))
    }
}