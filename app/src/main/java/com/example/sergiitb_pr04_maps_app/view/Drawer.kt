package com.example.sergiitb_pr04_maps_app.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sergiitb_pr04_maps_app.R
import com.example.sergiitb_pr04_maps_app.screensFromDrawer

@Composable
fun Drawer(
    modifier: Modifier = Modifier,
    onDestinationClicked: (route: String) -> Unit
) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier
                .fillMaxSize()
        ) {

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF966DE7),
                                Color(0xFF755CD4),
                                Color(0xFF4C48C1)
                            )
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = modifier.padding(15.dp)) {
                    Text(
                        text = "Arvind Meshram",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.Magenta
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "meshramaravind@gmail.com",
                        //style = MaterialTheme.typography.body2,
                        color = Color.Magenta
                    )
                }

                Image(
                    painter = painterResource(R.drawable.itb),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )

            }

            screensFromDrawer.forEach { screen ->
                Spacer(Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onDestinationClicked(screen.route) })
                        .height(40.dp)
                        .background(color = Color.Transparent)

                ) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = "Favorite Icon",
                        modifier = Modifier
                            .height(35.dp)
                            .width(35.dp)
                            .padding(start = 10.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = screen.title,
                        fontSize = 16.sp,
                        color = Color.Green,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
    }
}