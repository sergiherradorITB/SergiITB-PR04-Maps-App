package com.example.sergiitb_pr04_maps_app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sergiitb_pr04_maps_app.view.Camara
import com.example.sergiitb_pr04_maps_app.view.EditMarkerScreen
import com.example.sergiitb_pr04_maps_app.view.ListMarkersScreen
import com.example.sergiitb_pr04_maps_app.view.LoginScreen
import com.example.sergiitb_pr04_maps_app.view.MapScreen
import com.example.sergiitb_pr04_maps_app.view.MenuScreen
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navigationController = rememberNavController()
            val mapViewModel by viewModels<MapViewModel>()

            NavHost(
                navController = navigationController,
                startDestination = Routes.LogScreen.route
            ) {
                composable(Routes.MenuScreen.route) {
                    MenuScreen(mapViewModel, navigationController)
                }
                composable(Routes.MapScreen.route) {
                    MapScreen(navigationController, mapViewModel)
                }
                composable(Routes.ListMarkersScreen.route) {
                    ListMarkersScreen(navigationController, mapViewModel)
                }
                composable(Routes.Camara.route) {
                    Camara(navigationController, mapViewModel)
                }
                composable(Routes.EditMarker.route) {
                    EditMarkerScreen(navigationController, mapViewModel)
                }
                composable(Routes.LogScreen.route) {
                    LoginScreen(navigationController, mapViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDrawerWithFloatingButton(
    navController: NavController,
    mapViewModel: MapViewModel,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet {
            Text(text = "Mega Menú del Mapita", modifier = Modifier.padding(16.dp))
            Divider()
            IconButton(
                onClick = {
                    scope.launch {
                        state.close()
                    }
                }
            ) {
                Icon(
                    Icons.Default.Close, // Usar el icono de cierre adecuado
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current

                screensFromDrawer.forEach { screen ->
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        shape = RectangleShape,
                        onClick = {
                            if (screen.route == "cerrar_sesion") {
                                mapViewModel.signOut(context,navController)
                            } else {
                                navController.navigate(screen.route)
                                scope.launch { state.close() }
                            }
                        }
                    ) {
                        Text(text = screen.title)
                    }
                }
            }
        }
    }) {
        MyScaffold(mapViewModel, state, navController, content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDrawer(
    navController: NavController,
    mapViewModel: MapViewModel,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet {
            Text(text = "Mega Menú del Mapita", modifier = Modifier.padding(16.dp))
            Divider()
            IconButton(
                onClick = {
                    scope.launch {
                        state.close()
                    }
                }
            ) {
                Icon(
                    Icons.Default.Close, // Usar el icono de cierre adecuado
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current

                screensFromDrawer.forEach { screen ->
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        shape = RectangleShape,
                        onClick = {
                            if (screen.route == "cerrar_sesion") {
                                mapViewModel.signOut(context,navController)
                            } else {
                                navController.navigate(screen.route)
                                scope.launch { state.close() }
                            }
                        }
                    ) {
                        Text(text = screen.title)
                    }
                }
            }
        }
    }) {
        MyScaffold(mapViewModel, state, navController, content)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyScaffold(
    mapViewModel: MapViewModel,
    state: DrawerState,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = { MyTopAppBar(mapViewModel, state, navController) },
    ) {
        Box(Modifier.padding(it)) {
            content() // Llamar al contenido pasado
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(mapViewModel: MapViewModel, state: DrawerState, navController: NavController) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MAPITA ©",
                    modifier = Modifier.weight(1f),
                    color = Color.Black,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Routes.MapScreen.route)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Back",
                )
            }
        }
    )
}