package com.clementcorporation.levosonusii.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.LSFAB
import com.clementcorporation.levosonusii.model.LSUserInfo

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING.dp),
        color = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        Scaffold(
            backgroundColor = Color.LightGray,
            topBar = {
                LSAppBar(employeeName = viewModel.getDataStore().data.collectAsState(initial =
                LSUserInfo()).value.name
                )
            },
            floatingActionButton = {
                LSFAB()
            },
            floatingActionButtonPosition = FabPosition.End,
        ) {
        }
    }
    /*
        TODO:
            -create logout functionality (onBackPressed asks to log out when on HomeScreen)
            -use Scaffold for topBar/FAB
            -select equipment
            -select department
            -load voice profile
     */
}