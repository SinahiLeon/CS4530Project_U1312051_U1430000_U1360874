package com.example.klspaint

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MyAppNav (myNavController: NavHostController, startDestination: String){

    val drawingViewModel: DrawingViewModel = viewModel()

    NavHost(myNavController, startDestination){
        composable ("splash") { SplashScreen(myNavController) }
        composable("canvas") { CanvasScreen(drawingViewModel, myNavController) }}
}