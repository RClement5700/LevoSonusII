package com.clementcorporation.levosonusii.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.ENABLED_BUTTON_COLOR
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.LSFAB
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    BackHandler {
        viewModel.signOut()
        viewModel.viewModelScope.launch {
            delay(2000L)
            navController.navigate(LevoSonusScreens.LoginScreen.name)
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING.dp),
        elevation = ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            topBar = {
                LSAppBar(navController = navController, expandMenu = viewModel.expandMenu, employeeName = viewModel.getDataStore().data.collectAsState(initial = LSUserInfo()).value.name,
                    onClickSignOut = {
                        viewModel.signOut()
                        viewModel.viewModelScope.launch {
                            delay(2000L)
                            navController.navigate(LevoSonusScreens.LoginScreen.name)
                        }
                    }
                )
            },
            floatingActionButton = {
                LSFAB()
            },
            floatingActionButtonPosition = FabPosition.End,
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PADDING.dp),
                shape = RoundedCornerShape(CURVATURE.dp),
                backgroundColor = Color.White,
                elevation = ELEVATION.dp,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.showProgressBar.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.zIndex(1f).size(50.dp),
                            strokeWidth = 2.dp,
                            color = ENABLED_BUTTON_COLOR
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NavTile(title = "VOICE PROFILE") {
                            navController.navigate(LevoSonusScreens.CreateVoiceProfileScreen.name)
                        }
                        NavTile(title = "EQUIPMENT") { //SearchBar: filters equipment based on number input; display list of equipment in use; attach forklift/EPJ icon
                            navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                        }
                        NavTile(title = "DEPARTMENTS") { //choose department, display remaining orders & number of users in each department
                            navController.navigate(LevoSonusScreens.DepartmentsScreen.name)
                        }
                        NavTile(title = "HEALTH & WELLNESS") { //breaks; lunch; rewards; time-off; biometrics: steps, heartrate, etc
                            navController.navigate(LevoSonusScreens.HealthAndWellnessScreen.name)
                        }
                        NavTile(title = "PAY & BENEFITS") {//in app text messaging
                            navController.navigate(LevoSonusScreens.PayAndBenefitsScreen.name)
                        }
                        NavTile(title = "ORDERS") { //currentOrder: remaining picks, goBacks, currentPick, nextPick; PastOrders
                            navController.navigate(LevoSonusScreens.OrdersScreen.name)
                        }
                        NavTile(title = "MESSAGES") {//in app text messaging
                            navController.navigate(LevoSonusScreens.MessagesScreen.name)
                        }
                        NavTile(title = "ANNOUNCEMENTS") {//company wide & local announcements
                            navController.navigate(LevoSonusScreens.AnnouncementsScreen.name)
                        }
                        NavTile(title = "GAME CENTER") {//Casino where employees can place wagers using points accumulated by various tasks
                            navController.navigate(LevoSonusScreens.GameCenterScreen.name)
                        }
                    }
                }
            }
        }
    }
}