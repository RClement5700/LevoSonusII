package com.clementcorporation.levosonusii.screens.departments

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.home.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DepartmentsScreen(navController: NavController) {
    val hsViewModel: HomeScreenViewModel = hiltViewModel()
    BackHandler {
        hsViewModel.viewModelScope.launch {
            navController.popBackStack()
            navController.navigate(LevoSonusScreens.HomeScreen.name)
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        elevation = ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            topBar = {
                LSAppBar(navController = navController, expandMenu = hsViewModel.expandMenu,
                    title = "Departments",
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
                            navController.navigate(LevoSonusScreens.HomeScreen.name)
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.verticalScroll(enabled = true, state = rememberScrollState())) {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = LS_BLUE, thickness = 2.dp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                NavTile(title = "Grocery", icon = R.drawable.grocery_icon, showIcon = remember{mutableStateOf(true)})
                NavTile(title = "Meat", icon = R.drawable.meat_icon1, showIcon = remember{mutableStateOf(true)})
                NavTile(title = "Seafood", icon = R.drawable.seafood_icon, showIcon = remember{mutableStateOf(true)})
                NavTile(title = "Dairy", icon = R.drawable.dairy_icon, showIcon = remember{mutableStateOf(true)})
                NavTile(title = "Produce", icon = R.drawable.produce_icon, showIcon = remember{mutableStateOf(true)})
                NavTile(title = "Freezer", icon = R.drawable.freezer_icon, showIcon = remember{mutableStateOf(true)})
                NavTile(title = "Miscellaneous", icon = R.drawable.misc_icon, showIcon = remember{mutableStateOf(true)})
            }
        }
    }
}