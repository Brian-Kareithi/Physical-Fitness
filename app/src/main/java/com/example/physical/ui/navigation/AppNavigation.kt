package com.example.physical.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.physical.ui.auth.AuthViewModel
import com.example.physical.ui.auth.LoginScreen
import com.example.physical.ui.auth.SignUpScreen
import com.example.physical.ui.home.HomeScreen
import com.example.physical.ui.home.HomeViewModel
import com.example.physical.ui.nutrition.NutritionScreen
import com.example.physical.ui.progress.ProgressScreen
import com.example.physical.ui.runs.RunTrackingScreen
import com.example.physical.ui.runs.RunViewModel
import com.example.physical.ui.sleep.SleepScreen
import com.example.physical.ui.sleep.SleepViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object MorningRuns : Screen("morning_runs", "Morning Runs")
    object EveningRuns : Screen("evening_runs", "Evening Runs")
    object Nutrition : Screen("nutrition", "Kenyan Foods")
    object Sleep : Screen("sleep", "Sleep Schedule")
    object Progress : Screen("progress", "My Progress")
}

private val drawerItems = listOf(
    Screen.Home,
    Screen.MorningRuns,
    Screen.EveningRuns,
    Screen.Nutrition,
    Screen.Sleep,
    Screen.Progress
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val authState by authViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val homeViewModel: HomeViewModel = viewModel()
    val morningRunViewModel: RunViewModel = viewModel()
    val eveningRunViewModel: RunViewModel = viewModel()
    val sleepViewModel: SleepViewModel = viewModel()

    val firstName = authState.userName.substringBefore(" ").ifBlank { "User" }

    if (!authState.isLoggedIn) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLogin = { email, password -> authViewModel.login(email, password) },
                    onNavigateToSignUp = { navController.navigate("signup") },
                    onContinueAsGuest = { authViewModel.continueAsGuest() },
                    isLoading = authState.isLoading,
                    error = authState.error
                )
            }
            composable("signup") {
                SignUpScreen(
                    onSignUp = { name, email, password ->
                        authViewModel.signUp(name, email, password)
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                    isLoading = authState.isLoading,
                    error = authState.error
                )
            }
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                Text(
                                    text = "\u2715",
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Text(
                            text = "Menu",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        drawerItems.forEach { screen ->
                            DrawerItem(
                                title = screen.title,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            if (authState.isGuest) {
                                Text(
                                    text = "Guest",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Signed in as guest",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            } else {
                                Text(
                                    text = firstName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = authState.userName.ifBlank { firstName },
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    authViewModel.logout()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(
                                    text = "Logout",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Text(text = "\u2630", fontSize = 20.sp)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = homeViewModel,
                            userName = authState.userName,
                            isGuest = authState.isGuest
                        )
                    }
                    composable(Screen.MorningRuns.route) {
                        RunTrackingScreen(
                            viewModel = morningRunViewModel,
                            runType = "morning",
                            title = "Morning Runs",
                            color = Color(0xFFFF9800),
                            userName = authState.userName,
                            isGuest = authState.isGuest
                        )
                    }
                    composable(Screen.EveningRuns.route) {
                        RunTrackingScreen(
                            viewModel = eveningRunViewModel,
                            runType = "evening",
                            title = "Evening Runs",
                            color = Color(0xFF1565C0),
                            userName = authState.userName,
                            isGuest = authState.isGuest
                        )
                    }
                    composable(Screen.Nutrition.route) {
                        NutritionScreen()
                    }
                    composable(Screen.Sleep.route) {
                        SleepScreen(
                            viewModel = sleepViewModel,
                            isGuest = authState.isGuest
                        )
                    }
                    composable(Screen.Progress.route) {
                        ProgressScreen(
                            userName = authState.userName,
                            isGuest = authState.isGuest
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    title: String,
    onClick: () -> Unit
) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp)
    )
}
