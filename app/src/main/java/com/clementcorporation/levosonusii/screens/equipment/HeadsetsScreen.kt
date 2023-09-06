package com.clementcorporation.levosonusii.screens.equipment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.SelectableTile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.viewmodels.EquipmentScreenViewModel

private const val TAG = "HeadsetsScreen"
@Composable
fun HeadsetsScreen(navController: NavController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: EquipmentScreenViewModel = hiltViewModel()
    viewModel.retrieveHeadsetsData()

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
                    title = stringResource(id = R.string.headsets_screen_toolbar_title),
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
                                .fillMaxHeight(0.9f),
                            state = rememberLazyListState()
                        ) {
                            viewModel.headsetLiveData.observe(lifecycleOwner) { headsets ->
                                items(headsets, key = { it.id }) { headset ->
                                    SelectableTile(
                                        title = headset.serialNumber,
                                        isSelected = headset.isSelected,
                                        icon = R.drawable.headset_icon
                                    ) {
                                        headsets.forEach {
                                            it.isSelected.value = false
                                        }
                                        headset.isSelected.value = !headset.isSelected.value
                                        viewModel.setSelectedHeadsetId(headset.id)
                                    }
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
                                viewModel.updateHeadsetData()
                                navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                            }) {
                            if (viewModel.showProgressBar.value) {
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
    }
}