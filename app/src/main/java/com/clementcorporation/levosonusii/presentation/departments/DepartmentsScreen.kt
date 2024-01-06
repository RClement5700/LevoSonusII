package com.clementcorporation.levosonusii.presentation.departments

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.Department
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.Constants.BTN_HEIGHT
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens

private const val TAG = "DepartmentsScreen"
@Composable
fun DepartmentsScreen(navController: NavController) {
    val viewModel: DepartmentsViewModel = hiltViewModel()
    val uiState: DepartmentsScreenUiState by viewModel.departmentsScreenEventsStateFlow.collectAsStateWithLifecycle()
    BackHandler {
        viewModel.signOut {
            navController.clearBackStack(LevoSonusScreens.LoadingScreen.name)
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
                    title = stringResource(id = R.string.departments_screen_title_text),
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.signOut {
                            navController.clearBackStack(LevoSonusScreens.LoadingScreen.name)
                        }
                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                    }
                )
            }
        ) { paddingValue ->
            Log.e(TAG, paddingValue.toString())
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState is DepartmentsScreenUiState.Loading) {
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
                    if (uiState is DepartmentsScreenUiState.DataRetrieved) {
                        val departments = (uiState as DepartmentsScreenUiState.DataRetrieved).data
                        items(departments) { department ->
                            DepartmentTile(department) {
                                departments.forEach {
                                    it.isSelected.value = false
                                }
                                department.isSelected.value = !department.isSelected.value
                                viewModel.setSelectedDepartment(department.id)
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
                        disabledBackgroundColor = Color.Gray
                    ),
                    onClick = {
                        viewModel.updateUserDepartment()
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }) {
                    if (uiState is DepartmentsScreenUiState.Loading) {
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