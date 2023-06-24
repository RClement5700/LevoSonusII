package com.clementcorporation.levosonusii.screens.departments

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.TAG
import com.clementcorporation.levosonusii.screens.home.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DepartmentsScreen(navController: NavController, lifecycleOwner: LifecycleOwner) {
    val hsViewModel: HomeScreenViewModel = hiltViewModel()
    val departmentsViewModel: DepartmentsViewModel = viewModel()
    val currentDepartmentId = remember {
         mutableStateOf("")
    }
    val dataStore = hsViewModel.getUserInfo()
    val userInfo = dataStore.data.collectAsState(initial = LSUserInfo()).value
    val voiceProfile = hsViewModel.getVoiceProfile().data.collectAsState(initial = VoiceProfile()).value
    departmentsViewModel.fetchCurrentDepartmentId(userInfo)
    departmentsViewModel.currentDepartmentIdLiveData.observe(lifecycleOwner) {
        currentDepartmentId.value = it
    }

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
        ) { paddingValue ->
            Log.e(TAG, paddingValue.toString())
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (departmentsViewModel.showProgressBar.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .zIndex(1f)
                            .size(50.dp),
                        strokeWidth = 2.dp,
                        color = LS_BLUE
                    )
                }
            }
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    color = LS_BLUE,
                    thickness = 2.dp,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(0.dp, 4.dp, 0.dp, 0.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight(.9f)) {
                    departmentsViewModel.departmentsLiveData.observe(lifecycleOwner) { departments ->
                        items(departments) { department ->
                            department.isSelected.value = userInfo.departmentId == department.id
                            DepartmentTile(department) {
                                departments.forEach {
                                    it.isSelected.value = false
                                }
                                department.isSelected.value = !department.isSelected.value
                                departmentsViewModel.setSelectedDepartment(department.id, userInfo, dataStore)
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier.padding(PADDING.dp).fillMaxWidth().fillMaxHeight(),
                    shape = RoundedCornerShape(CURVATURE),
                    elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = LS_BLUE,
                        disabledBackgroundColor = Color.LightGray
                    ),
                    onClick = {
                        departmentsViewModel.updateUserDepartment(currentDepartmentId.value, userInfo, voiceProfile)
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }) {
                    if(departmentsViewModel.showProgressBar.value) {
                        CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text(
                            text = stringResource(id = R.string.btn_text_apply),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DepartmentIcon(modifier: Modifier, url: String) {
    Image(
        modifier = modifier,
        painter = rememberImagePainter(data = url, builder = {
                crossfade(false)
                placeholder(R.drawable.levosonus_rocket_logo)
        }),
        contentDescription = "Department Icon",
        contentScale = ContentScale.Crop
    )
}

@Composable
fun DepartmentTile(department: Department, onClick: () -> Unit = {}) {
    val backgroundColor = if (department.isSelected.value) Color.Cyan else Color.White
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PADDING.dp)
            .clickable(onClick = onClick),
        elevation = ELEVATION.dp,
        shape = RoundedCornerShape(CURVATURE.dp),
        backgroundColor = backgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    DepartmentIcon(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(Constants.PADDING.dp),
                        url = department.iconUrl
                    )
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = department.title,
                        color = LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(PADDING.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.electric_pallet_jack_icon),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Electric Pallet Jack Icon"
                    )
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = "Order Pickers:",
                        color = LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = department.orderPickersCount,
                        color = LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                    )

                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.forklift_icon),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Forklift Icon"
                    )
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = "Forklifts:",
                        color = LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = department.forkliftCount,
                        color = LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = "Remaining Orders:",
                        color = LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = department.remainingOrders,
                        color = LS_BLUE,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                    )
                }
            }
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    tint = LS_BLUE,
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = ""
                )
            }
        }
    }
}