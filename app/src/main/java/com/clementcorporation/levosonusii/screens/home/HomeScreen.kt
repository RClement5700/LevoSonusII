package com.clementcorporation.levosonusii.screens.home

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.Constants.STORAGE_APPENDED_URL
import com.clementcorporation.levosonusii.main.Constants.STORAGE_BASE_URL
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val voiceProfileUrl = viewModel.getUserInfo().data.collectAsState(initial = LSUserInfo()).value.profilePicUrl
    val imageUrl = "${STORAGE_BASE_URL}$voiceProfileUrl${STORAGE_APPENDED_URL}"
    val inflateProfilePic = remember{
        mutableStateOf(false)
    }
    BackHandler {
        viewModel.viewModelScope.launch {
            viewModel.signOut()
            delay(2000L)
            navController.popBackStack()
            navController.navigate(LevoSonusScreens.LoginScreen.name)
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
                LSAppBar(navController = navController, expandMenu = viewModel.expandMenu,
                    title = viewModel.getUserInfo().data.collectAsState(initial = LSUserInfo()).value.name,
                    profilePicUrl = imageUrl,
                    onClickSignOut = {
                        viewModel.viewModelScope.launch {
                            viewModel.signOut()
                            delay(2000L)
                            navController.popBackStack()
                            navController.navigate(LevoSonusScreens.LoginScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        inflateProfilePic.value = !inflateProfilePic.value
                    }
                )
            }
        ) {
            Log.e(TAG, it.toString())
            InflatableProfilePic(inflateProfilePic = inflateProfilePic, imageUrl = imageUrl)
            HomeScreenContent(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun InflatableProfilePic(inflateProfilePic: MutableState<Boolean>, imageUrl: String) {
    val configuration = LocalConfiguration.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .padding(32.dp),
        contentAlignment = Alignment.TopStart
    ) {
        if (inflateProfilePic.value) {
            Card(
                modifier = Modifier.padding(PADDING.dp),
                elevation = ELEVATION.dp,
                shape = RoundedCornerShape(CURVATURE.dp),
                border = BorderStroke(2.dp, Color.LightGray)
            ) {
                Box(
                    modifier = when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(.25f)
                        }
                        else -> {
                            Modifier.fillMaxSize(.75f)
                        }
                    },
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(PADDING.dp)
                            .size(25.dp)
                            .zIndex(1f),
                        enabled = true,
                        onClick = { inflateProfilePic.value = false }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = "Close Profile Picture"
                        )
                    }
                    Image(
                        painter = rememberImagePainter(data = imageUrl, builder = {
                            crossfade(false)
                            placeholder(R.drawable.levosonus_rocket_logo)
                        }),
                        contentDescription = "Inflated Profile Picture",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(navController: NavController, viewModel: HomeScreenViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.showProgressBar.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .zIndex(1f)
                    .size(50.dp),
                strokeWidth = 2.dp,
                color = LS_BLUE
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavTile(title = stringResource(id = R.string.homescreen_tile_voice_profile_label)) {
                navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_equipment_label)) { //SearchBar: filters equipment based on number input; display list of equipment in use; attach forklift/EPJ icon
                navController.navigate(LevoSonusScreens.EquipmentScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_departments_label)) { //choose department, display remaining orders & number of users in each department
                navController.navigate(LevoSonusScreens.DepartmentsScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_health_wellness_label)) { //breaks; lunch; rewards; time-off; biometrics: steps, heartrate, etc
                navController.navigate(LevoSonusScreens.HealthAndWellnessScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_pay_benefits_label)) {//in app text messaging
                navController.navigate(LevoSonusScreens.PayAndBenefitsScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_orders_label)) { //currentOrder: remaining picks, goBacks, currentPick, nextPick; PastOrders
                navController.navigate(LevoSonusScreens.OrdersScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_messages_label)) {//in app text messaging
                navController.navigate(LevoSonusScreens.MessagesScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_announcements_label)) {//company wide & local announcements
                navController.navigate(LevoSonusScreens.AnnouncementsScreen.name)
            }
            NavTile(title = stringResource(id = R.string.homescreen_tile_game_center_label)) {//Casino where employees can place wagers using points accumulated by various tasks
                navController.navigate(LevoSonusScreens.GameCenterScreen.name)
            }
        }
    }
}