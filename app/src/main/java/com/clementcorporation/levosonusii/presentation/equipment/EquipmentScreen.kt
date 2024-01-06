package com.clementcorporation.levosonusii.presentation.equipment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.NavTile

private const val TAG = "EquipmentScreen"
@Composable
fun EquipmentScreen(navController: NavController) {
    val viewModel: EquipmentScreenViewModel = hiltViewModel()
    BackHandler {
        navController.popBackStack()
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
                LSAppBar(navController = navController, expandMenu = viewModel.expandMenu,
                    title = stringResource(id = R.string.equipment_screen_toolbar_title_text),
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.signOut {
                            navController.clearBackStack(LevoSonusScreens.LoadingScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                    }
                )
            }
        ) {
            Log.e(TAG, it.toString())
            Column(modifier = Modifier.verticalScroll(enabled = true, state = rememberScrollState())) {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = LS_BLUE, thickness = 2.dp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                NavTile(title = stringResource(id = R.string.equipment_screen_headsets_tile_title_text), icon = R.drawable.headset_icon, showIcon = remember {
                    mutableStateOf(true)
                }) {
                    navController.navigate(LevoSonusScreens.HeadsetsScreen.name)
                }
                NavTile(title = stringResource(id = R.string.equipment_screen_scanners_tile_title_text), icon = R.drawable.scanner_icon, showIcon = remember {
                    mutableStateOf(true)
                }) {
                    navController.navigate(LevoSonusScreens.ProductScannersScreen.name)
                }
                NavTile(title = stringResource(id = R.string.equipment_screen_machines_tile_title_text), icon = R.drawable.forklift_icon, showIcon = remember {
                    mutableStateOf(true)
                }) {
                    navController.navigate(LevoSonusScreens.MachinesScreen.name)
                }
            }
        }
    }
}