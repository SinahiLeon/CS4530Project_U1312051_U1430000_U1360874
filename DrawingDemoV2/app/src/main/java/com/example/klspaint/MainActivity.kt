package com.example.klspaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.klspaint.ui.theme.DrawingDemoV2Theme
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingDemoV2Theme {
                val navCon = rememberNavController()
                MyAppNav(navCon, "splash")
            }
        }
    }
}

