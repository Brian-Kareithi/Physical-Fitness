package com.example.physical

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.physical.data.repository.ActivityTracker
import com.example.physical.ui.auth.AuthViewModel
import com.example.physical.ui.navigation.AppNavigation
import com.example.physical.ui.splash.SplashScreen
import com.example.physical.ui.theme.PhysicalTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            startTracking()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ActivityTracker.instance = ActivityTracker(this)
        requestPermissions()

        setContent {
            PhysicalTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    val authViewModel: AuthViewModel = viewModel()
                    AppNavigation(authViewModel = authViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasPermissions()) {
            startTracking()
        }
    }

    override fun onPause() {
        super.onPause()
        stopTracking()
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            requestPermissionLauncher.launch(needed.toTypedArray())
        }
    }

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTracking() {
        ActivityTracker.instance?.startTracking()
    }

    private fun stopTracking() {
        ActivityTracker.instance?.stopTracking()
    }
}
