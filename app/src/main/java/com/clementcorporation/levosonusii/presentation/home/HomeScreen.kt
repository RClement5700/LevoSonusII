package com.clementcorporation.levosonusii.presentation.home

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.domain.models.NavTileData
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LSSurface
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.NavTile
import kotlinx.coroutines.Dispatchers

//TODO: Cache the profile pic into a bitmap and store the bitmap in dataStore for memory purposes
// Look into the lengthy load times
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val userState = viewModel.getSessionDataStore().data.collectAsStateWithLifecycle(
        initialValue = LSUserInfo(),
        lifecycle = LocalLifecycleOwner.current.lifecycle,
        context = Dispatchers.IO
    ).value
    val title = userState.name
    val profilePicUrl = userState.profilePicUrl
    BackHandler {
        viewModel.signOut {
            navController.navigate(LevoSonusScreens.LoginScreen.name)
        }
    }
    LSSurface {
        Scaffold(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize(),
            topBar = {
                LSAppBar(
                    navController = navController,
                    expandMenu = viewModel.expandMenu,
                    title = title,
                    profilePicUrl = profilePicUrl,
                    onClickSignOut = {
                        viewModel.signOut {
                            navController.navigate(LevoSonusScreens.LoginScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        viewModel.inflateProfilePic = !viewModel.inflateProfilePic
                    }
                )
            }
        )
        { paddingValues ->
            if (viewModel.inflateProfilePic) InflatableProfilePic(viewModel = viewModel, imageUrl = profilePicUrl)
            HomeScreenContent(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun InflatableProfilePic(viewModel: HomeScreenViewModel, imageUrl: String) {
    val configuration = LocalConfiguration.current
    Surface(
        shadowElevation = 8.dp,
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
            IconButton (
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
fun HomeScreenContent(modifier: Modifier, navController: NavController, viewModel: HomeScreenViewModel) {
    Box(
        modifier = modifier.fillMaxSize(),
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
        val appsDataList = listOf(
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_voice_profile_label)
            ) {
                navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_equipment_label)
            ) {
                navController.navigate(LevoSonusScreens.EquipmentScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_departments_label)
            ) {
                navController.navigate(LevoSonusScreens.DepartmentsScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_health_wellness_label)
            ) {
                navController.navigate(LevoSonusScreens.HealthAndWellnessScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_pay_benefits_label)
            ) {
                navController.navigate(LevoSonusScreens.PayAndBenefitsScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_orders_label)
            ) {
                navController.navigate(LevoSonusScreens.OrdersScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_messages_label)
            ) {
                navController.navigate(LevoSonusScreens.MessagesScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_announcements_label)
            ) {
                navController.navigate(LevoSonusScreens.AnnouncementsScreen.name)
            },
            NavTileData(
                title = stringResource(id = R.string.homescreen_tile_game_center_label)
            ) {
                navController.navigate(LevoSonusScreens.GameCenterScreen.name)
            }
        ).sortedBy { data -> data.title }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(appsDataList) { tileData ->
                NavTile(
                    title = tileData.title,
                    onClick = tileData.navigate
                )
            }
        }
    }
}