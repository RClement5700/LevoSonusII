package com.clementcorporation.levosonusii.screens.equipment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.BTN_HEIGHT
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.SelectableTile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.model.Equipment
import com.clementcorporation.levosonusii.screens.equipment.viewmodels.EquipmentScreenEvents
import com.clementcorporation.levosonusii.screens.equipment.viewmodels.EquipmentScreenViewModel
import kotlinx.coroutines.launch

private const val TAG = "MachinesScreen"
@Composable
fun MachinesScreen(navController: NavController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: EquipmentScreenViewModel = hiltViewModel()

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
                    title = stringResource(id = R.string.machines_screen_toolbar_title),
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
        ) { padding ->
            Log.e(TAG, padding.toString())
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
                        LazyColumn(modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)) {

                            lifecycleOwner.lifecycleScope.launch {
                                viewModel.equipmentScreenEventsFlow.collect { event ->
                                    if (event is EquipmentScreenEvents.OnRetrieveMachinesListData) {
                                        items(event.machines) { machine ->
                                            if (machine is Equipment.Forklift) {
                                                SelectableTile(
                                                    title = machine.serialNumber,
                                                    icon = R.drawable.forklift_icon,
                                                    isSelected = machine.isSelected
                                                ) {
                                                    event.machines.forEach {
                                                        (it as Equipment.Forklift).isSelected.value = false
                                                    }
                                                    machine.isSelected.value = !machine.isSelected.value
                                                    viewModel.setSelectedMachineId(machine.serialNumber)
                                                }
                                            } else {
                                                SelectableTile(
                                                    title = (machine as Equipment.ElectricPalletJack).serialNumber,
                                                    icon = R.drawable.electric_pallet_jack_icon,
                                                    isSelected = machine.isSelected
                                                ) {
                                                    event.machines.forEach {
                                                        (it as Equipment.ElectricPalletJack).isSelected.value =
                                                            false
                                                    }
                                                    machine.isSelected.value = !machine.isSelected.value
                                                    viewModel.setSelectedMachineId(machine.serialNumber)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            modifier = Modifier
                                .padding(Constants.PADDING.dp)
                                .fillMaxWidth()
                                .height(BTN_HEIGHT.dp),
                            shape = RoundedCornerShape(Constants.CURVATURE),
                            elevation = ButtonDefaults.elevation(defaultElevation = Constants.ELEVATION.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Constants.LS_BLUE,
                                disabledBackgroundColor = Color.LightGray
                            ),
                            onClick = {
                                viewModel.updateMachinesData()
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