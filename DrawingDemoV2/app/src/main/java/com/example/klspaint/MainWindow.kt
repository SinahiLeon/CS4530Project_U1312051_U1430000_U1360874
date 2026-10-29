package com.example.klspaint

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.verticalScroll

@Composable
fun MainWindow(
    klsViewModel: KLSViewModel,
    myNavController: NavHostController
) {

    // get saved drawings from Room
    val drawings by klsViewModel.allDrawings.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)

    )

    {
        Spacer(modifier = Modifier.height(20.dp))

        Text("My Drawings")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            klsViewModel.createNewDrawing()
            myNavController.navigate("canvas")
        }) {
            Text("New Drawing")
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Display saved drawings
        for (drawing in drawings) {
            if (drawing != null) {
                Button (onClick = {
                    klsViewModel.loadDrawing(drawing)
                    myNavController.navigate("canvas")
                }) {
                    Text("Drawing ${drawing.id}")
                }
            }
        }
    }
}
