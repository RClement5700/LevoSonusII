package com.clementcorporation.levosonusii.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.LSFAB
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING.dp),
        elevation = ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        Scaffold(
            backgroundColor = Color.White,
            topBar = {
                LSAppBar(employeeName = viewModel.getDataStore().data.collectAsState(initial = LSUserInfo()).value.name)
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