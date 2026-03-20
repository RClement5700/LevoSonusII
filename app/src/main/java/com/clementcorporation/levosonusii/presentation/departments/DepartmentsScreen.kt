package com.clementcorporation.levosonusii.presentation.departments

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants.BTN_HEIGHT
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LSSurface
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DepartmentsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: DepartmentsViewModel = hiltViewModel()
    val uiState = viewModel.departmentsScreenEventsStateFlow.collectAsStateWithLifecycle().value
    val isHandlingDbUpdate = remember { mutableStateOf(false) }
    BackHandler {
        navController.popBackStack()
    }
    LSSurface {
        Scaffold(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize(),
            topBar = {
                LSAppBar(
                    expandMenu = viewModel.expandMenu,
                    title = stringResource(id = R.string.departments_screen_title_text),
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.signOut {
                            CoroutineScope(Dispatchers.Main).launch {
                                navController.navigate(LevoSonusScreens.LoginScreen.name) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    },
                    onClickLeftIcon = {
                        navController.navigate(LevoSonusScreens.HomeScreen.name)
                    }
                )
            }
        ) { paddingValue ->
            when (uiState) {

                is DepartmentsScreenUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValue.calculateTopPadding()),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .zIndex(1f)
                                .size(50.dp),
                            strokeWidth = 2.dp,
                            color = LS_BLUE
                        )
                    }
                }

                is DepartmentsScreenUiState.DataRetrieved -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValue.calculateTopPadding()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(
                            color = LS_BLUE,
                            thickness = 2.dp,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.9f)
                        ) {
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
                        Button(
                            modifier = Modifier
                                .padding(PADDING.dp)
                                .fillMaxWidth()
                                .height(BTN_HEIGHT.dp),
                            shape = RoundedCornerShape(CURVATURE),
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = ELEVATION.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LS_BLUE,
                                contentColor = LS_BLUE,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.Gray
                            ),
                            onClick = {
                                viewModel.updateUsersDepartment()
                                Toast.makeText(context, "Department: ${viewModel.getCurrentDepartment().title}", Toast.LENGTH_SHORT).show()
                                navController.navigate(LevoSonusScreens.HomeScreen.name)
                            }) {
                            if (isHandlingDbUpdate.value) {
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

                is DepartmentsScreenUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValue.calculateTopPadding()),
                        contentAlignment = Alignment.Center
                    )  {
                        Column(
                            modifier = Modifier
                                .clickable { viewModel.fetchDepartmentsData() },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.failed_to_load_error_message),
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Try Again",
                                tint = Color.Green
                            )
                        }
                    }
                }

                is DepartmentsScreenUiState.OnApplyButtonClicked -> {
                    isHandlingDbUpdate.value = true
                }

                is DepartmentsScreenUiState.OnDataUpdated -> {
                    isHandlingDbUpdate.value = true
                }
            }
        }
    }
}

@Composable
fun DepartmentTile(
    index: Int,
    icon: Int,
    title: String,
    forklifts: Int,
    orderPickers: Int,
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
                }
            ),
        color = if (index == viewModel.selectedIndex) Color.Cyan else Color.White,
        shadowElevation = 8.dp,
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
                    tint = Color.Black,
                    contentDescription = stringResource(id = R.string.departments_screen_department_icon_content_description)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
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
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.departments_screen_department_remaining_orders_label, remainingOrders),
                    color = Color.Black,
                    style = MaterialTheme.typography.bodySmall
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
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.departments_screen_department_total_orders_label, totalOrders),
                    color = Color.Black,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}