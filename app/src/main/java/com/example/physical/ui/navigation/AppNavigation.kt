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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

sealed class Screen(val route: String, val title: String, val icon: String) {
    object Home : Screen("home", "Dashboard", "\u2302")
    object Runs : Screen("runs", "Runs", "\u25B7")
    object Nutrition : Screen("nutrition", "Kenyan Foods", "\u2615")
    object Sleep : Screen("sleep", "Sleep Schedule", "\u25CB")
    object Progress : Screen("progress", "My Progress", "\u2606")
}

private val drawerItems = listOf(
    Screen.Home,
    Screen.Runs,
    Screen.Nutrition,
    Screen.Sleep,
    Screen.Progress
)

fun String.capitalizeFirst(): String =
    if (isBlank()) this else replaceFirstChar { it.uppercase() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val authState by authViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val homeViewModel: HomeViewModel = viewModel()
    val runViewModel: RunViewModel = viewModel()
    val sleepViewModel: SleepViewModel = viewModel()

    val firstName = authState.userName
        .substringBefore(" ")
        .ifBlank { "User" }
        .capitalizeFirst()

    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF1B5E20), Color(0xFF388E3C))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Physical",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                    Text(
                                        text = "\u2715",
                                        fontSize = 20.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        drawerItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            DrawerItem(
                                icon = screen.icon,
                                title = screen.title,
                                isSelected = isSelected,
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

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
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
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            } else {
                                Text(
                                    text = firstName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = authState.userName.capitalizeFirst(),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                                    containerColor = Color(0xFFF5F5F5),
                                    contentColor = Color(0xFF616161)
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = "Log out",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        userName = authState.userName,
                        isGuest = authState.isGuest,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Runs.route) {
                    RunTrackingScreen(
                        viewModel = runViewModel,
                        userName = authState.userName,
                        isGuest = authState.isGuest,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Nutrition.route) {
                    NutritionScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Sleep.route) {
                    SleepScreen(
                        viewModel = sleepViewModel,
                        isGuest = authState.isGuest,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable(Screen.Progress.route) {
                    ProgressScreen(
                        userName = authState.userName,
                        isGuest = authState.isGuest,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: String,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFFE8F5E9) else Color.Transparent
    val textColor = if (isSelected) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
    val weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 18.sp,
            color = textColor.copy(alpha = if (isSelected) 1f else 0.6f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = weight,
            color = textColor
        )
    }
}
