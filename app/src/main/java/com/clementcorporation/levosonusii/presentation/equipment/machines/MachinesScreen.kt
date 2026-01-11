package com.clementcorporation.levosonusii.presentation.equipment.machines

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens

private const val TAG = "MachinesScreen"
@Composable
fun MachinesScreen(navController: NavController) {
    //TODO: Add a searchbar that filters the list with a typing delay of 3 seconds because operators
    //      will likely find their machine then want to match the serial number on the machine
    //      to a corresponding number on screen rather than scrolling through a list
    val viewModel: MachinesScreenViewModel = hiltViewModel()

    BackHandler {
        navController.popBackStack()
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        shadowElevation = Constants.ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize(),
            topBar = {
                LSAppBar(navController = navController, expandMenu = viewModel.expandMenu,
                    title = stringResource(id = R.string.machines_screen_toolbar_title),
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.signOut {
                            navController.clearBackStack(LevoSonusScreens.LoginScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                    }
                )
            }
        ) { padding ->
            Log.e(TAG, padding.toString())
        }
    }
}
