package com.clementcorporation.levosonusii.screens.home

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.main.SelectableTile
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.TAG

@Composable
fun HomeScreen(navController: NavController, lifecycleOwner: LifecycleOwner) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val dataStore = viewModel.getUserInfo()
    val userInfo = dataStore.data.collectAsState(initial = LSUserInfo()).value
    val voiceProfile = viewModel.getVoiceProfile().data.collectAsState(initial = VoiceProfile()).value
    val profilePicUrl = userInfo.profilePicUrl
    val showOperatorTypeWindow = remember { mutableStateOf(false) }
    val inflateProfilePic = remember { mutableStateOf(false) }
    val operatorType = remember { mutableStateOf("")}
    viewModel.retrieveOperatorType(userInfo, dataStore)
    viewModel.operatorTypeLiveData.observe(lifecycleOwner) {
        showOperatorTypeWindow.value = it.isEmpty()
    }
    BackHandler {
        viewModel.signOut()
        navController.popBackStack()
        navController.navigate(LevoSonusScreens.LoginScreen.name)
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
                    title = dataStore.data.collectAsState(initial = LSUserInfo()).value.name,
                    profilePicUrl = profilePicUrl,
                    onClickSignOut = {
                        viewModel.signOut()
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                    },
                    onClickLeftIcon = {
                        inflateProfilePic.value = !inflateProfilePic.value
                    }
                )
            }
        ) { paddingValues ->
            Log.e(TAG, paddingValues.toString())
            if (showOperatorTypeWindow.value) ChooseOperatorTypeWindow(viewModel, showOperatorTypeWindow,
                dataStore, voiceProfile, userInfo, operatorType
            )
            InflatableProfilePic(inflateProfilePic = inflateProfilePic, imageUrl = profilePicUrl)
            HomeScreenContent(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun ChooseOperatorTypeWindow(
    viewModel: HomeScreenViewModel, showWindow: MutableState<Boolean>, dataStore: DataStore<LSUserInfo>,
    voiceProfile: VoiceProfile, userInfo: LSUserInfo, operatorType: MutableState<String>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.5f)
                .padding(PADDING.dp)
                .zIndex(1f),
            elevation = ELEVATION.dp,
            shape = RoundedCornerShape(CURVATURE.dp),
            backgroundColor = Color.White,
            border = BorderStroke(2.dp, LS_BLUE)
        ) {
            Column(
                modifier = Modifier.padding(PADDING.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                val operatorTypes = listOf(
                    OperatorType(title = stringResource(id = R.string.operator_type_order_picker_tile_text),
                        icon = R.drawable.electric_pallet_jack_icon, isSelected = remember { mutableStateOf(false) }),
                    OperatorType(title = stringResource(id = R.string.operator_type_forklift_tile_text),
                        icon = R.drawable.forklift_icon, isSelected = remember { mutableStateOf(false) })
                )
                Text(
                    text = stringResource(id = R.string.operator_type_title_text),
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    color = LS_BLUE,
                    thickness = 2.dp,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)) {
                    items(operatorTypes) { type ->
                        SelectableTile(type.title, type.icon, type.isSelected) {
                            operatorTypes.forEach {
                                it.isSelected.value = false
                            }
                            type.isSelected.value = !type.isSelected.value
                            operatorType.value = type.title
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(PADDING.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(CURVATURE),
                    elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = LS_BLUE,
                        disabledBackgroundColor = Color.LightGray
                    ),
                    onClick = {
                        if (operatorType.value.isNotEmpty()) {
                            viewModel.updateOperatorType(
                                dataStore,
                                userInfo,
                                voiceProfile,
                                operatorType.value
                            )
                            showWindow.value = false
                        } else {
                            Toast.makeText(context, context.getString(R.string.operator_type_toast_message), Toast.LENGTH_SHORT).show()
                        }
                    }) {
                    if(viewModel.showProgressBar.value) {
                        CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text(
                            text = stringResource(id = R.string.btn_text_apply),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(PADDING.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(CURVATURE),
                    border = BorderStroke(2.dp, LS_BLUE),
                    elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        disabledBackgroundColor = Color.LightGray
                    ),
                    onClick = {
                        showWindow.value = false
                    }) {
                        Text(
                            text = stringResource(id = R.string.operator_type_secondary_button_text),
                            color = LS_BLUE,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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