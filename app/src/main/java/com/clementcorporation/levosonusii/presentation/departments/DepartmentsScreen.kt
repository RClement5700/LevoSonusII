package com.clementcorporation.levosonusii.presentation.departments

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
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
    val uiState = viewModel.departmentsScreenEventsStateFlow.collectAsStateWithLifecycle().value
    BackHandler {
        viewModel.signOut {
            navController.popBackStack()
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
                            navController.popBackStack()
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)) {
                    if (uiState is DepartmentsScreenUiState.DataRetrieved) {
                        val departments = uiState.data
                        itemsIndexed(departments) { index, department ->
                            DepartmentTile(
                                index = index,
                                title = department.title,
                                icon = department.icon,
                                totalOrders = department.totalOrders,
                                remainingOrders = department.remainingOrders,
                                forklifts = department.forklifts,
                                orderPickers = department.orderPickers,
                                viewModel = viewModel
                            )
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
                        viewModel.updateUsersDepartment()
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
fun DepartmentTile(
    index: Int,
    icon: Int = R.drawable.scanner_icon,
    title: String,
    forklifts: String,
    orderPickers: String,
    remainingOrders: String,
    totalOrders: String,
    viewModel: DepartmentsViewModel
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .selectable(
                selected = index == viewModel.selectedIndex,
                onClick = {
                    if (viewModel.selectedIndex != index) viewModel.selectedIndex = index
                    else viewModel.selectedIndex = -1
                }
            ),
        color = if (index == viewModel.selectedIndex) Color.Cyan else Color.White,
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(id = icon),
                    contentDescription = stringResource(id = R.string.departments_screen_department_icon_content_description)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.Black,
                    style = MaterialTheme.typography.h6
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.departments_screen_department_forklift_operator_label, forklifts),
                    color = Color.Black,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = stringResource(id = R.string.departments_screen_department_remaining_orders_label, remainingOrders),
                    color = Color.Black,
                    style = MaterialTheme.typography.body1
                )

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.departments_screen_department_electric_pallet_jack_operator_label, orderPickers),
                    color = Color.Black,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = stringResource(id = R.string.departments_screen_department_total_orders_label, totalOrders),
                    color = Color.Black,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}