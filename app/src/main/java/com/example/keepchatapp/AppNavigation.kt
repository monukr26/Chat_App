package com.example.keepchatapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class AppScreen (val route : String) {
    object Splash : AppScreen("splash")
    object Chat : AppScreen("chat")
    object Login : AppScreen("login")
    object SignUp : AppScreen("signup")
}


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppNavigation( navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppScreen.Splash.route) {
        composable(AppScreen.Splash.route) {
            SplashScreen(navController)
        }
        composable(AppScreen.Login.route) {
            LoginScreen(navController, viewModel()
            )
        }
        composable(AppScreen.SignUp.route) {
            SignUpScreen(navController, viewModel())
        }
        composable(AppScreen.Chat.route) {
            val chatViewModel: ChatViewModel = viewModel()
            ChatPage(
                navController = navController,
                viewModel = chatViewModel,
                authModel = viewModel()
            )

        }


    }

}