package com.clementcorporation.levosonusii.presentation.equipment.machines

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenUiState
import com.clementcorporation.levosonusii.presentation.equipment.SearchableEquipmentInputField
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.Constants.SELECTED_COLOR
import com.clementcorporation.levosonusii.util.EquipmentTile
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.LevoSonusUtil
import com.clementcorporation.levosonusii.util.LevoSonusUtil.navigateAfterSignOut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MachinesScreen(navController: NavController) {
    //TODO: Add a dropdown menu next to the SearchBar that selects the machineType so users only filter by the selected machineType
    val configuration = LocalConfiguration.current
    val viewModel: MachinesScreenViewModel = hiltViewModel()
    val uiState = viewModel.equipmentScreenUiState.collectAsStateWithLifecycle(
        initialValue = EquipmentScreenUiState.OnLoading,
        lifecycle = LocalLifecycleOwner.current.lifecycle,
        context = Dispatchers.IO
    ).value
    BackHandler {
        navController.popBackStack()
    }
    Scaffold(
        modifier = Modifier
            .padding(top = LevoSonusUtil.getTopPaddingPerConfiguration(configuration))
            .fillMaxSize(),
        topBar = {
            LSAppBar(
                expandMenu = viewModel.expandMenu,
                title = stringResource(id = R.string.machines_screen_toolbar_title),
                profilePicUrl = null,
                onClickSignOut = {
                    viewModel.signOut {
                        navigateAfterSignOut(navController)
                    }
                },
                onClickLeftIcon = {
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentAlignment = Alignment.Center
        ) {
            when(uiState) {
                is EquipmentScreenUiState.OnLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .zIndex(1f)
                            .size(50.dp),
                        strokeWidth = 2.dp,
                        color = LS_BLUE
                    )
                }

                is EquipmentScreenUiState.OnDataRetrieved -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
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
                            SearchableEquipmentInputField(
                                viewModel = viewModel,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(PADDING.dp),
                                onFilterButtonClicked = {
                                    viewModel.wasFilterButtonClicked = true
                                    viewModel.expandMachineTypeMenu.value = !viewModel.expandMachineTypeMenu.value
                                },
                                onSortButtonClicked = {
                                    viewModel.wasSortButtonClicked = true
                                    viewModel.expandMachineTypeMenu.value = !viewModel.expandMachineTypeMenu.value
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(end = 24.dp)
                                    .zIndex(1f)
                            ) {
                                DropdownMenu(
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .fillMaxHeight(0.3f),
                                    scrollState = rememberScrollState(),
                                    expanded = viewModel.expandMachineTypeMenu.value,
                                    shape = RoundedCornerShape(8.dp),
                                    properties = PopupProperties(
                                        dismissOnBackPress = false,
                                        dismissOnClickOutside = true,
                                        focusable = true
                                    ),
                                    onDismissRequest = {
                                        viewModel.wasSortButtonClicked = false
                                        viewModel.wasFilterButtonClicked = false
                                        viewModel.expandMachineTypeMenu.value = false
                                    }
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        text =
                                            if (viewModel.wasSortButtonClicked) "Sort By:"
                                            else if (viewModel.wasFilterButtonClicked) "Filter By:"
                                            else "Sort By:"
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        color = LS_BLUE
                                    )
                                    LazyColumn(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(250.dp)
                                    ) {
                                        itemsIndexed(viewModel.getMenuItems()) { index, item ->
                                            DropdownMenuItem(
                                                text = {
                                                    Surface(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(8.dp)
                                                            .selectable(
                                                                selected = index == viewModel.getMenuIndex(),
                                                                onClick = { viewModel.setMenuIndex(index) }
                                                            ),
                                                        color = if (index == viewModel.getMenuIndex()) SELECTED_COLOR else Color.White,
                                                        shadowElevation = 8.dp,
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        OutlinedButton(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(32.dp),
                                                            shape = RoundedCornerShape(CURVATURE),
                                                            onClick = { viewModel.setMenuIndex(index) },
                                                            border = BorderStroke(0.dp, if (index == viewModel.getMenuIndex() ) SELECTED_COLOR else Color.Transparent),
                                                            colors = ButtonDefaults.outlinedButtonColors(
                                                                containerColor = if (index == viewModel.getMenuIndex() ) SELECTED_COLOR else Color.Transparent,
                                                                contentColor = LS_BLUE
                                                            )
                                                        ) {
                                                            Text(
                                                                text = item,
                                                                color = LS_BLUE,
                                                                fontWeight = FontWeight.Bold,
                                                                textAlign = TextAlign.Start,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {}
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        modifier = Modifier
                                            .padding(
                                                top = LevoSonusUtil.setPaddingPerConfiguration(configuration, 24, 0),
                                                start = PADDING.dp,
                                                end = PADDING.dp
                                            )
                                            .fillMaxSize(),
                                        shape = RoundedCornerShape(CURVATURE),
                                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = ELEVATION.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LS_BLUE,
                                            contentColor = LS_BLUE,
                                            disabledContainerColor = Color.Gray,
                                            disabledContentColor = Color.Gray
                                        ),
                                        onClick = {
                                            //viewModel.onApplyButtonClicked()
                                        }) {
                                        Text(
                                            text = stringResource(id = R.string.btn_text_apply),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    OutlinedButton(
                                        modifier = Modifier
                                            .padding(
                                                top = LevoSonusUtil.setPaddingPerConfiguration(configuration, 24, 0),
                                                start = PADDING.dp,
                                                end = PADDING.dp
                                            )
                                            .fillMaxSize(),
                                        shape = RoundedCornerShape(CURVATURE),
                                        onClick = { /* Handle click */ },
                                        border = BorderStroke(1.dp, LS_BLUE),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.Blue
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.btn_text_clear),
                                            color = LS_BLUE,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(
                                        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                                            0.65f else 0.9f
                                    )
                            ) {
                                val scanners = uiState.data
                                itemsIndexed(scanners) { index, scanner ->
                                    EquipmentTile(
                                        viewModel = viewModel,
                                        index = index,
                                        uiModel = scanner,
                                        alreadySelected = index == 0
                                    )
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .padding(
                                        top = LevoSonusUtil.setPaddingPerConfiguration(configuration, 24, 0),
                                        start = PADDING.dp,
                                        end = PADDING.dp
                                    )
                                    .fillMaxSize(),
                                shape = RoundedCornerShape(CURVATURE),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = ELEVATION.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LS_BLUE,
                                    contentColor = LS_BLUE,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.Gray
                                ),
                                onClick = {
                                    viewModel.onApplyButtonClicked()
                                }) {
                                if (viewModel.isHandlingDbUpdate) {
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

                is EquipmentScreenUiState.OnDataUpdated -> {
                    val context = navController.context
                    LaunchedEffect(navController) {
                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.machines_screen_headset_success_toast_message,
                                viewModel.equipmentList[viewModel.selectedIndex].serialNumber

                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        CoroutineScope(Dispatchers.Main).launch {
                            navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                        }
                    }
                }

                is EquipmentScreenUiState.OnFailedToLoadData -> {
                    Column(
                        modifier = Modifier
                            .clickable { viewModel.fetchMachinesData() },
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
        }
    }
}
