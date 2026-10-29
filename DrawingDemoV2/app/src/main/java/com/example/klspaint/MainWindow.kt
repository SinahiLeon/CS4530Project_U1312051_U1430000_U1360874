package com.example.klspaint

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text

@Composable
fun MainWindow(klsViewModel : KLSViewModel) {   //means MainWindow receives same ViewModel used by drawing screen
    val drawings by klsViewModel.allDrawings.collectAsState()   //observes State Flow and converts into Compose state
    Column{
         Text("My Drawings")
         Text("Saved Drawings: ${drawings.size}")
    }

}