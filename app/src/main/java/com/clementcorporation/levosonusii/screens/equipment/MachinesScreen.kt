package com.clementcorporation.levosonusii.screens.equipment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.SelectionTile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.viewmodels.EquipmentScreenViewModel
import com.clementcorporation.levosonusii.screens.home.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "Machines Screen"
@Composable
fun MachinesScreen(navController: NavController) {
    val hsViewModel: HomeScreenViewModel = hiltViewModel()
    val equipmentScreenViewModel: EquipmentScreenViewModel = viewModel()

    BackHandler {
        hsViewModel.viewModelScope.launch {
            navController.popBackStack()
            navController.navigate(LevoSonusScreens.EquipmentScreen.name)
        }
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
                    title = "Machines",
                    profilePicUrl = null,
                    onClickSignOut = {
                        hsViewModel.viewModelScope.launch {
                            hsViewModel.signOut()
                            delay(2000L)
                            navController.popBackStack()
                            navController.navigate(LevoSonusScreens.LoginScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        hsViewModel.viewModelScope.launch {
                            navController.popBackStack()
                            navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                        }
                    }
                )
            }
        ) {
            Log.e(TAG, it.toString())
            Column(modifier = Modifier.verticalScroll(enabled = true, state = rememberScrollState())) {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = Constants.LS_BLUE, thickness = 2.dp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn{
                    val selection = -1
                    item {
                        SelectionTile()
                    }
                }
                equipmentScreenViewModel.electricPalletJx.collectAsState().value.forEach { epj ->
                    SelectionTile(title = epj.serialNumber, icon = R.drawable.electric_pallet_jack_icon)
                }
                equipmentScreenViewModel.forklifts.collectAsState().value.forEach { forklift ->
                    SelectionTile(title = forklift.serialNumber, icon = R.drawable.forklift_icon)
                }
            }
        }
    }
}