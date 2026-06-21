package com.example.physical

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.physical.ui.auth.AuthViewModel
import com.example.physical.ui.navigation.AppNavigation
import com.example.physical.ui.theme.PhysicalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhysicalTheme {
                val authViewModel: AuthViewModel = viewModel()
                AppNavigation(authViewModel = authViewModel)
            }
        }
    }
}
