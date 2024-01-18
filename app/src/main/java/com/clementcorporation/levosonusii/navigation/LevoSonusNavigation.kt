package com.clementcorporation.levosonusii.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.clementcorporation.levosonusii.presentation.departments.DepartmentsScreen
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreen
import com.clementcorporation.levosonusii.presentation.equipment.headsets.HeadsetsScreen
import com.clementcorporation.levosonusii.presentation.equipment.machines.MachinesScreen
import com.clementcorporation.levosonusii.presentation.equipment.scanners.ScannersScreen
import com.clementcorporation.levosonusii.presentation.healthandwellness.HealthAndWellnessScreen
import com.clementcorporation.levosonusii.presentation.home.HomeScreen
import com.clementcorporation.levosonusii.presentation.loading.LoadingScreen
import com.clementcorporation.levosonusii.presentation.login.LoginScreen
import com.clementcorporation.levosonusii.presentation.messenger.MessengerScreen
import com.clementcorporation.levosonusii.presentation.orders.OrdersScreen
import com.clementcorporation.levosonusii.presentation.payandbenefits.PayAndBenefitsScreen
import com.clementcorporation.levosonusii.presentation.register.RegisterScreen
import com.clementcorporation.levosonusii.presentation.voiceprofile.VoiceProfileScreen
import com.clementcorporation.levosonusii.screens.GameCenterScreen
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun LevoSonusNavigation(navController: NavHostController, showFab: MutableState<Boolean>,
                        fusedLocationClient: FusedLocationProviderClient, showVoiceCommandActivity: (String) -> Unit) {
    NavHost(navController = navController, startDestination = LevoSonusScreens.LoadingScreen.name) {
        composable(LevoSonusScreens.LoadingScreen.name){
            LoadingScreen(navController, fusedLocationClient)
            showFab.value = false
        }
        composable(LevoSonusScreens.LoginScreen.name){
            LoginScreen(navController = navController)
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
            HomeScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.EquipmentScreen.name){
            EquipmentScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.DepartmentsScreen.name){
            DepartmentsScreen(navController = navController)
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
            MessengerScreen(navController = navController)
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
            MachinesScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.HeadsetsScreen.name){
            HeadsetsScreen(navController = navController)
            showFab.value = true
        }
        composable(LevoSonusScreens.ProductScannersScreen.name){
            ScannersScreen(navController = navController)
            showFab.value = true
        }
    }
}