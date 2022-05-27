package com.clementcorporation.levosonusii.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clementcorporation.levosonusii.screens.GameCenterScreen
import com.clementcorporation.levosonusii.screens.departments.DepartmentsScreen
import com.clementcorporation.levosonusii.screens.equipment.EquipmentScreen
import com.clementcorporation.levosonusii.screens.healthandwellness.HealthAndWellnessScreen
import com.clementcorporation.levosonusii.screens.home.HomeScreen
import com.clementcorporation.levosonusii.screens.login.LoginScreen
import com.clementcorporation.levosonusii.screens.messages.MessagesScreen
import com.clementcorporation.levosonusii.screens.orders.OrdersScreen
import com.clementcorporation.levosonusii.screens.payandbenefits.PayAndBenefitsScreen
import com.clementcorporation.levosonusii.screens.register.RegisterScreen
import com.clementcorporation.levosonusii.screens.splash.SplashScreen
import com.clementcorporation.levosonusii.screens.voiceprofile.VoiceProfileScreen

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
        composable(LevoSonusScreens.VoiceProfileScreen.name){
            VoiceProfileScreen(navController = navController)
        }
        composable(LevoSonusScreens.HomeScreen.name){
            HomeScreen(navController = navController)
        }
        composable(LevoSonusScreens.EquipmentScreen.name){
            EquipmentScreen(navController = navController)
        }
        composable(LevoSonusScreens.DepartmentsScreen.name){
            DepartmentsScreen(navController = navController)
        }
        composable(LevoSonusScreens.HealthAndWellnessScreen.name){
            HealthAndWellnessScreen(navController = navController)
        }
        composable(LevoSonusScreens.PayAndBenefitsScreen.name){
            PayAndBenefitsScreen(navController = navController)
        }
        composable(LevoSonusScreens.MessagesScreen.name){
            MessagesScreen(navController = navController)
        }
        composable(LevoSonusScreens.OrdersScreen.name){
            OrdersScreen(navController = navController)
        }
        composable(LevoSonusScreens.AnnouncementsScreen.name){
            AnnouncementsScreen(navController = navController)
        }
        composable(LevoSonusScreens.GameCenterScreen.name){
            GameCenterScreen(navController = navController)
        }
    }
}