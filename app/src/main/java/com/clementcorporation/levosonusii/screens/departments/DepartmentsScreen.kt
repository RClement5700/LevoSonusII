package com.clementcorporation.levosonusii.screens.departments

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.BTN_HEIGHT
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.TAG

@Composable
fun DepartmentsScreen(navController: NavController) {
    val departmentsViewModel: DepartmentsViewModel = hiltViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    BackHandler {
        navController.popBackStack()
        navController.navigate(LevoSonusScreens.HomeScreen.name)
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
                LSAppBar(navController = navController, expandMenu = departmentsViewModel.expandMenu,
                    title = stringResource(id = R.string.departments_screen_title_text),
                    profilePicUrl = null,
                    onClickSignOut = {
                        departmentsViewModel.signOut()
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.LoginScreen.name)

                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 4.dp, 0.dp, 0.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)) {
                    departmentsViewModel.departmentsLiveData.observe(lifecycleOwner) { departments ->
                        items(departments) { department ->
                            department.isSelected.value =
                                departmentsViewModel.getSessionDataStore().data.collectAsState(
                                    initial = LSUserInfo()
                                ).value.departmentId == department.id
                            DepartmentTile(department) {
                                departments.forEach {
                                    it.isSelected.value = false
                                }
                                department.isSelected.value = !department.isSelected.value
                                departmentsViewModel.setSelectedDepartment(department.id)
                            }
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(PADDING.dp)
                        .fillMaxWidth()
                        .height(BTN_HEIGHT.dp),
                    shape = RoundedCornerShape(CURVATURE),
                    elevation = ButtonDefaults.elevation(defaultElevation = ELEVATION.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = LS_BLUE,
                        disabledBackgroundColor = Color.LightGray
                    ),
                    onClick = {
                        departmentsViewModel.updateUserDepartment()
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
        contentDescription = stringResource(id = R.string.departments_screen_department_icon_content_description),
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
                        contentDescription = stringResource(id = R.string.departments_screen_department_icon_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = stringResource(id = R.string.departments_screen_department_electric_pallet_jack_operator_label),
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
                        contentDescription = stringResource(id = R.string.departments_screen_forklift_icon_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(Constants.PADDING.dp),
                        text = stringResource(id = R.string.departments_screen_department_forklift_operator_label),
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
                        text = stringResource(id = R.string.departments_screen_department_remaining_orders_label),
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