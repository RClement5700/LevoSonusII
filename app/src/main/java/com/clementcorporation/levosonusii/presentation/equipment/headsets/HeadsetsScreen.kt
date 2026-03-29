package com.clementcorporation.levosonusii.presentation.equipment.headsets

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.presentation.equipment.EquipmentScreenUiState
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.Constants.PADDING
import com.clementcorporation.levosonusii.util.EquipmentTile
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.LevoSonusUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun HeadsetsScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val viewModel: HeadsetsScreenViewModel = hiltViewModel()
    val uiState = viewModel.headsetsScreenUiState.collectAsStateWithLifecycle(
        initialValue = EquipmentScreenUiState.OnLoading,
        lifecycle = LocalLifecycleOwner.current.lifecycle,
        context = Dispatchers.IO
    ).value
    val isHandlingDbUpdate = remember { mutableStateOf(false) }
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
                title = stringResource(id = R.string.headsets_screen_toolbar_title),
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
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(
                                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                                        0.65f else 0.9f
                                )
                        ) {
                            val headsets = uiState.data
                            itemsIndexed(headsets) { index, headset ->
                                EquipmentTile(
                                    viewModel = viewModel,
                                    index = index,
                                    title = headset.serialNumber,
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
                                //viewModel.updateUsersDepartment() TODO: copy functionality for headsets
                                isHandlingDbUpdate.value = true
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

                is EquipmentScreenUiState.OnFailedToLoadData -> {
                    Column(
                        modifier = Modifier
                            .clickable { viewModel.fetchHeadsetsData() },
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