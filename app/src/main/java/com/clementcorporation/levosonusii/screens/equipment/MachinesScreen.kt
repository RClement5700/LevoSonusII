package com.clementcorporation.levosonusii.screens.equipment

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.BTN_HEIGHT
import com.clementcorporation.levosonusii.main.LSAppBar
import com.clementcorporation.levosonusii.main.SelectableTile
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.clementcorporation.levosonusii.model.VoiceProfile
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.clementcorporation.levosonusii.screens.equipment.model.Equipment
import com.clementcorporation.levosonusii.screens.equipment.viewmodels.EquipmentScreenViewModel
import com.clementcorporation.levosonusii.screens.equipment.viewmodels.EquipmentViewModelFactory
import com.clementcorporation.levosonusii.screens.home.HomeScreenViewModel

const val TAG = "Machines Screen"

@Composable
fun MachinesScreen(navController: NavController, lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    val hsViewModel: HomeScreenViewModel = hiltViewModel()
    val equipmentScreenViewModel: EquipmentScreenViewModel = EquipmentViewModelFactory(context.resources
        ).create(EquipmentScreenViewModel::class.java)
    val voiceProfile = hsViewModel.getVoiceProfile().data.collectAsState(initial = VoiceProfile()).value
    val dataStore = hsViewModel.getUserInfo()
    val userInfo = dataStore.data.collectAsState(initial = LSUserInfo()).value
    if (equipmentScreenViewModel.isForkliftOperator(userInfo)) equipmentScreenViewModel.retrieveForkliftsData(userInfo)
    else equipmentScreenViewModel.retrieveElectricPalletJacksData(userInfo)

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
                LSAppBar(navController = navController, expandMenu = hsViewModel.expandMenu,
                    title = "Machines",
                    profilePicUrl = null,
                    onClickSignOut = {
                        hsViewModel.signOut()
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
                if (equipmentScreenViewModel.showProgressBar.value) {
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
                        LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)) {
                            equipmentScreenViewModel.machineLiveData.observe(lifecycleOwner) { equipmentList ->
                                items(equipmentList) { equipment ->
                                    if (equipment is Equipment.Forklift) {
                                        SelectableTile(
                                            title = equipment.serialNumber,
                                            icon = R.drawable.forklift_icon,
                                            isSelected = equipment.isSelected
                                        ) {
                                            equipmentList.forEach {
                                                (it as Equipment.Forklift).isSelected.value = false
                                            }
                                            equipment.isSelected.value = !equipment.isSelected.value
                                            equipmentScreenViewModel.setSelectedMachineId(equipment.serialNumber)
                                        }
                                    } else {
                                        SelectableTile(
                                            title = (equipment as Equipment.ElectricPalletJack).serialNumber,
                                            icon = R.drawable.electric_pallet_jack_icon,
                                            isSelected = equipment.isSelected
                                        ) {
                                            equipmentList.forEach {
                                                (it as Equipment.ElectricPalletJack).isSelected.value =
                                                    false
                                            }
                                            equipment.isSelected.value = !equipment.isSelected.value
                                            equipmentScreenViewModel.setSelectedMachineId(equipment.serialNumber)
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
                                equipmentScreenViewModel.updateMachinesData(
                                    dataStore = dataStore,
                                    userInfo = userInfo,
                                    voiceProfile = voiceProfile
                                )
                                navController.navigate(LevoSonusScreens.EquipmentScreen.name)
                            }) {
                            if (equipmentScreenViewModel.showProgressBar.value) {
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