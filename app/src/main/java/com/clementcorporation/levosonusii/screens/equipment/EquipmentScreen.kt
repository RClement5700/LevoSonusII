package com.clementcorporation.levosonusii.screens.equipment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.home.HomeScreenViewModel

@Composable
fun EquipmentScreen(navController: NavController) {
    val hsViewModel: HomeScreenViewModel = hiltViewModel()

    BackHandler {
        navController.popBackStack()
        navController.navigate(LevoSonusScreens.HomeScreen.name)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        elevation = Constants.ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            topBar = {
                LSAppBar(navController = navController, expandMenu = hsViewModel.expandMenu,
                    title = "Equipment",
                    profilePicUrl = null,
                    onClickSignOut = {
                        hsViewModel.signOut()
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                )
            }
        ) {
            Log.e(TAG, it.toString())
            Column(modifier = Modifier.verticalScroll(enabled = true, state = rememberScrollState())) {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = LS_BLUE, thickness = 2.dp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                NavTile(title = "Headset", icon = R.drawable.headset_icon, showIcon = remember {
                    mutableStateOf(true)
                }) {
                    navController.navigate(LevoSonusScreens.HeadsetsScreen.name)
                }
                NavTile(title = "Scanner", icon = R.drawable.scanner_icon, showIcon = remember {
                    mutableStateOf(true)
                }) {
                    navController.navigate(LevoSonusScreens.ProductScannersScreen.name)
                }
                NavTile(title = "Machine", icon = R.drawable.forklift_icon, showIcon = remember {
                    mutableStateOf(true)
                }) {
                    navController.navigate(LevoSonusScreens.MachinesScreen.name)
                }
            }
        }
    }
}