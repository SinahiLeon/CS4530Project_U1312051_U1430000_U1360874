package com.example.klspaint

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer

@Composable
fun MainWindow(
    klsViewModel: KLSViewModel,
    myNavController: NavHostController
) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text("My Drawings")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            klsViewModel.createNewDrawing()
            myNavController.navigate("canvas")
        }) {
            Text("New Drawing")
        }
    }
}
