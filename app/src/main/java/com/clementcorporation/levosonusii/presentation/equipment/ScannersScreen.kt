package com.clementcorporation.levosonusii.presentation.equipment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.SelectableTile

private const val TAG = "ScannersScreen"

@Composable
fun ScannersScreen(navController: NavController) {
    val viewModel: EquipmentScreenViewModel = hiltViewModel()
    val uiState = viewModel.equipmentScreenEventsFlow.collectAsStateWithLifecycle().value
    viewModel.retrieveScannersData()

    BackHandler {
        navController.popBackStack()
        navController.navigate(LevoSonusScreens.EquipmentScreen.name)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        elevation = Constants.ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White,
            topBar = {
                LSAppBar(navController = navController, expandMenu = viewModel.expandMenu,
                    title = stringResource(id = R.string.scanners_screen_toolbar_title),
                    profilePicUrl = null,
                    onClickSignOut = {
                        viewModel.signOut()
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                    },
                    onClickLeftIcon = {
                        navController.popBackStack()
                        navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                    }
                )
            }
        ) { paddingValues ->
            Log.e(TAG, paddingValues.toString())
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
                        color = Constants.LS_BLUE
                    )
                }
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(
                        color = Constants.LS_BLUE,
                        thickness = 2.dp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (uiState) {
                        is EquipmentScreenEvents.OnRetrieveScannersListData -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f),
                                state = rememberLazyListState()
                            ) {
                                items(uiState.scanners) { scanner ->
                                    SelectableTile(
                                        title = scanner.serialNumber,
                                        isSelected = scanner.isSelected,
                                        icon = R.drawable.scanner_icon
                                    ) {
                                        uiState.scanners.forEach {
                                            it.isSelected.value = false
                                        }
                                        scanner.isSelected.value = !scanner.isSelected.value
                                        viewModel.setSelectedScannerId(scanner.id)
                                    }
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .padding(Constants.PADDING.dp)
                                    .fillMaxWidth()
                                    .height(Constants.BTN_HEIGHT.dp),
                                shape = RoundedCornerShape(Constants.CURVATURE),
                                elevation = ButtonDefaults.elevation(defaultElevation = Constants.ELEVATION.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Constants.LS_BLUE,
                                    disabledBackgroundColor = Color.LightGray
                                ),
                                onClick = {
                                    viewModel.updateScannerData()
                                    navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                                }) {
                                if (viewModel.showProgressBar.value) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        text = stringResource(id = R.string.btn_text_apply),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}