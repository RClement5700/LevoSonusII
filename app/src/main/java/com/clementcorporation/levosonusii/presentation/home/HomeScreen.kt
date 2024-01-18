package com.clementcorporation.levosonusii.presentation.home

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.NavTile


//TODO: Cache the profile pic into a bitmap and store in dataStore for memory purposes
private const val TAG = "HomeScreen"
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val userState = viewModel.getSessionDataStore().data.collectAsStateWithLifecycle(initialValue = LSUserInfo()).value
    val title = userState.name
    val profilePicUrl = userState.profilePicUrl
    BackHandler {
        viewModel.signOut {
            navController.navigate(LevoSonusScreens.LoadingScreen.name)
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
                    title = title,
                    profilePicUrl = profilePicUrl,
                    onClickSignOut = {
                        viewModel.signOut {
                            navController.navigate(LevoSonusScreens.LoadingScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        viewModel.inflateProfilePic = !viewModel.inflateProfilePic
                    }
                )
            }
        ) { paddingValues ->
            Log.e(TAG, paddingValues.toString())
            if (viewModel.inflateProfilePic) InflatableProfilePic(viewModel = viewModel, imageUrl = profilePicUrl)
            HomeScreenContent(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun InflatableProfilePic(viewModel: HomeScreenViewModel, imageUrl: String) {
    val configuration = LocalConfiguration.current
    Surface(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.25f)
                    .zIndex(1f)
            }
            else -> {
                Modifier
                    .fillMaxSize(.75f)
                    .zIndex(1f)
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                modifier = Modifier
                    .padding(PADDING.dp)
                    .size(25.dp)
                    .zIndex(1f),
                enabled = true,
                onClick = { viewModel.inflateProfilePic = false }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = stringResource(id = R.string.home_screen_close_profile_picture_content_description)
                )
            }
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(false)
                            placeholder(R.drawable.levosonus_rocket_logo)
                        }).build()
                ),
                contentDescription = stringResource(id = R.string.home_screen_profile_picture_content_description),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
fun HomeScreenContent(navController: NavController, viewModel: HomeScreenViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.showProgressBar) {
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
                navController.navigate(LevoSonusScreens.MessengerScreen.name)
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