package com.clementcorporation.levosonusii.presentation.equipment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.domain.models.LSUserInfo
import com.clementcorporation.levosonusii.util.Constants.LS_BLUE
import com.clementcorporation.levosonusii.util.LSAppBar
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.clementcorporation.levosonusii.util.LevoSonusUtil
import com.clementcorporation.levosonusii.util.LevoSonusUtil.navigateAfterSignOut
import com.clementcorporation.levosonusii.util.NavTile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private fun onBackPressed(
    navController: NavController,
    profilePicUrl: String?
) {
    val encodedUrl = URLEncoder.encode(profilePicUrl, StandardCharsets.UTF_8.toString())
    CoroutineScope(Dispatchers.Main).launch {
        navController.navigate("${LevoSonusScreens.HomeScreen.name}/$encodedUrl")
    }
}
@Composable
fun EquipmentScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val viewModel: EquipmentScreenViewModel = hiltViewModel()
    val userInfo = viewModel.getSessionDataStore().data.collectAsState(initial = LSUserInfo()).value
    val profilePicUrl = userInfo.profilePicUrl
    BackHandler {
        onBackPressed(navController, profilePicUrl)
    }

    Scaffold(
        modifier = Modifier
            .padding(top = LevoSonusUtil.getTopPaddingPerConfiguration(configuration))
            .fillMaxSize(),
        topBar = {
            LSAppBar(
                expandMenu = viewModel.expandMenu,
                title = stringResource(id = R.string.equipment_screen_toolbar_title_text),
                profilePicUrl = null,
                onClickSignOut = {
                    viewModel.signOut {
                        navigateAfterSignOut(navController)
                    }
                },
                onClickLeftIcon = {
                    onBackPressed(navController, profilePicUrl)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(
                    enabled = true,
                    state = rememberScrollState()
                )
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = LS_BLUE, thickness = 2.dp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            NavTile(title = stringResource(id = R.string.equipment_screen_headsets_tile_title_text), icon = R.drawable.headset_icon, showIcon = remember {
                mutableStateOf(true)
            }) {
                navController.navigate(LevoSonusScreens.HeadsetsScreen.name)
            }
            NavTile(title = stringResource(id = R.string.equipment_screen_scanners_tile_title_text), icon = R.drawable.scanner_icon, showIcon = remember {
                mutableStateOf(true)
            }) {
                navController.navigate(LevoSonusScreens.ProductScannersScreen.name)
            }
            NavTile(title = stringResource(id = R.string.equipment_screen_machines_tile_title_text), icon = R.drawable.forklift_icon, showIcon = remember {
                mutableStateOf(true)
            }) {
                navController.navigate(LevoSonusScreens.MachinesScreen.name)
            }
        }
    }
}