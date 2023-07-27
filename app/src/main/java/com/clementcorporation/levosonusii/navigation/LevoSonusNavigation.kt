package com.clementcorporation.levosonusii.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.clementcorporation.levosonusii.screens.GameCenterScreen
import com.clementcorporation.levosonusii.screens.departments.DepartmentsScreen
import com.clementcorporation.levosonusii.screens.equipment.EquipmentScreen
import com.clementcorporation.levosonusii.screens.equipment.HeadsetsScreen
import com.clementcorporation.levosonusii.screens.equipment.MachinesScreen
import com.clementcorporation.levosonusii.screens.equipment.ScannersScreen
import com.clementcorporation.levosonusii.screens.healthandwellness.HealthAndWellnessScreen
import com.clementcorporation.levosonusii.screens.home.HomeScreen
import com.clementcorporation.levosonusii.screens.login.LoginScreen
import com.clementcorporation.levosonusii.screens.messages.MessengerScreen
import com.clementcorporation.levosonusii.screens.orders.OrdersScreen
import com.clementcorporation.levosonusii.screens.payandbenefits.PayAndBenefitsScreen
import com.clementcorporation.levosonusii.screens.register.RegisterScreen
import com.clementcorporation.levosonusii.screens.splash.SplashScreen
import com.clementcorporation.levosonusii.screens.voiceprofile.VoiceProfileScreen

@Composable
fun LevoSonusNavigation(navController: NavHostController, showFab: MutableState<Boolean>,
                        lifecycleOwner: LifecycleOwner, showVoiceCommandActivity: (String) -> Unit) {
    NavHost(navController = navController, startDestination = LevoSonusScreens.SplashScreen.name) {
        composable(LevoSonusScreens.SplashScreen.name){
            SplashScreen(navController)
            showFab.value = false
        }
        composable(LevoSonusScreens.LoginScreen.name){
            LoginScreen(navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.RegisterScreen.name){
            RegisterScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.VoiceProfileScreen.name){
            VoiceProfileScreen(navController = navController, showVoiceCommandActivity)
            showFab.value = true
        }
        composable(LevoSonusScreens.HomeScreen.name){
            HomeScreen(navController = navController, lifecycleOwner)
            showFab.value = true
        }
        composable(LevoSonusScreens.EquipmentScreen.name){
            EquipmentScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.DepartmentsScreen.name){
            DepartmentsScreen(navController = navController, lifecycleOwner)
            showFab.value = true
        }
        composable(LevoSonusScreens.HealthAndWellnessScreen.name){
            HealthAndWellnessScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.PayAndBenefitsScreen.name){
            PayAndBenefitsScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.MessengerScreen.name){
            MessengerScreen(navController = navController, lifecycleOwner = lifecycleOwner)
            showFab.value = true
        }
        composable(LevoSonusScreens.OrdersScreen.name){
            OrdersScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.AnnouncementsScreen.name){
            AnnouncementsScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.GameCenterScreen.name){
            GameCenterScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.MachinesScreen.name){
            MachinesScreen(navController = navController, lifecycleOwner)
            showFab.value = true
        }
        composable(LevoSonusScreens.HeadsetsScreen.name){
            HeadsetsScreen(navController = navController, lifecycleOwner)
            showFab.value = true
        }
        composable(LevoSonusScreens.ProductScannersScreen.name){
            ScannersScreen(navController = navController, lifecycleOwner = lifecycleOwner)
            showFab.value = true
        }
    }
}