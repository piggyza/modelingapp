package com.example.modelbookingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.modelbookingapp.data.model.UserType
import com.example.modelbookingapp.ui.screens.admin.AdminDashboardScreen
import com.example.modelbookingapp.ui.screens.auth.LoginScreen
import com.example.modelbookingapp.ui.screens.auth.RegisterScreen
import com.example.modelbookingapp.ui.screens.booking.BookingScreen
import com.example.modelbookingapp.ui.screens.client.ClientHomeScreen
import com.example.modelbookingapp.ui.screens.model.ModelProfileScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ClientHome : Screen("client_home")
    object ModelProfile : Screen("model_profile")
    object AdminDashboard : Screen("admin_dashboard")
    object Booking : Screen("booking/{modelId}") {
        fun createRoute(modelId: String) = "booking/$modelId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { userType ->
                    val destination = when (userType) {
                        UserType.CLIENT -> Screen.ClientHome.route
                        UserType.MODEL -> Screen.ModelProfile.route
                        UserType.ADMIN -> Screen.AdminDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = { userType ->
                    val destination = when (userType) {
                        UserType.CLIENT -> Screen.ClientHome.route
                        UserType.MODEL -> Screen.ModelProfile.route
                        UserType.ADMIN -> Screen.AdminDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ClientHome.route) {
            ClientHomeScreen(
                onModelClick = { modelId ->
                    navController.navigate(Screen.Booking.createRoute(modelId))
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ModelProfile.route) {
            ModelProfileScreen(
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Booking.route) { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId") ?: return@composable
            BookingScreen(
                modelId = modelId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBookingComplete = {
                    navController.navigate(Screen.ClientHome.route) {
                        popUpTo(Screen.ClientHome.route) { inclusive = true }
                    }
                }
            )
        }
    }
}