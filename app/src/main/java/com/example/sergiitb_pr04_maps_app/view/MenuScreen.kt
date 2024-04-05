package com.example.sergiitb_pr04_maps_app.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.MyDrawer
import com.example.sergiitb_pr04_maps_app.Routes
import com.example.sergiitb_pr04_maps_app.viewmodel.MapViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuScreen(mapViewModel: MapViewModel, navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        MyDrawer(
            navController,
            mapViewModel = mapViewModel,
            content = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Bienvenido al Menú", fontSize = 20.sp)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(Routes.MapScreen.route)
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(text = "Mapa")
                        }
                        Button(
                            onClick = {
                                navController.navigate(Routes.ListMarkersScreen.route)
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(text = "Listar marcadores")
                        }
                        Button(
                            onClick = {
                                mapViewModel.signOut(navController)
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(text = "Cerrar Sesión")
                        }
                    }
                }
            }
        )
    }
}