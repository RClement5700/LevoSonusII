package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.home.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VoiceProfileScreen(navController: NavController) {
    val viewModel: VoiceProfileViewModel = hiltViewModel()
    val hsViewModel: HomeScreenViewModel = hiltViewModel()
    BackHandler {
        viewModel.viewModelScope.launch {
            navController.popBackStack()
            navController.navigate(LevoSonusScreens.HomeScreen.name)
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
                    title = "Voice Profile",
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.viewModelScope.launch {
                            hsViewModel.signOut()
                            delay(2000L)
                            navController.popBackStack()
                            navController.navigate(LevoSonusScreens.LoginScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        viewModel.viewModelScope.launch {
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
                viewModel.getVoiceProfileDataStore().data.collectAsState(
                    initial = VoiceProfile()
                ).value.voiceProfileMap.keys.forEach {
                    NavTile(title = it) {

                    }
                }
            }
        }
    }
}