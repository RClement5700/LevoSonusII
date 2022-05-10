package com.clementcorporation.levosonusii.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.screens.login.LoginScreen
import com.clementcorporation.levosonusii.screens.splash.SplashScreen

@Composable
fun LevoSonusNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LevoSonusScreens.SplashScreen.name) {
        composable(LevoSonusScreens.SplashScreen.name){
            SplashScreen(navController)
        }
        composable(LevoSonusScreens.LoginScreen.name){
            LoginScreen(navController)
        }
        composable(LevoSonusScreens.RegisterScreen.name){
        }
        composable(LevoSonusScreens.HomeScreen.name){
        }
    }
}