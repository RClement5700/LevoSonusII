package com.clementcorporation.levosonusii.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.LSFAB
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val expandMenu = remember {
        mutableStateOf(false)
    }
    val showProgressBar = remember {
        mutableStateOf(false)
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
                LSAppBar(navController = navController, expandMenu = expandMenu, employeeName = viewModel.getDataStore().data.collectAsState(initial = LSUserInfo()).value.name,
                    onClickSignOut = {
                        Firebase.auth.signOut()
                        showProgressBar.value = true
                        expandMenu.value = false
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showProgressBar.value) CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = Constants.ENABLED_BUTTON_COLOR
                )
            }
        }
    }
    /*
        TODO:
            -create logout functionality (onBackPressed asks to log out when on HomeScreen)
            -select equipment
            -select department
            -load voice profile
     */
}