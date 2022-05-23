package com.clementcorporation.levosonusii.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.screens.home.HomeScreen
import com.clementcorporation.levosonusii.screens.login.LoginScreen
import com.clementcorporation.levosonusii.screens.register.RegisterScreen
import com.clementcorporation.levosonusii.screens.splash.SplashScreen
import com.clementcorporation.levosonusii.screens.voiceprofile.CreateVoiceProfileScreen

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
            RegisterScreen(navController = navController)
        }
        composable(LevoSonusScreens.CreateVoiceProfileScreen.name){
            CreateVoiceProfileScreen(navController = navController)
        }
        composable(LevoSonusScreens.HomeScreen.name){
            HomeScreen()
        }
    }
}