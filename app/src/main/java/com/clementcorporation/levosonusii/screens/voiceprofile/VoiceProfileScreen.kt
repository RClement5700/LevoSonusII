package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.NavTile
import com.clementcorporation.levosonusii.model.VoiceProfile

@Composable
fun VoiceProfileScreen(navController: NavController) {
    val viewModel: VoiceProfileViewModel = hiltViewModel()
    Card(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        backgroundColor = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            viewModel.getVoiceProfileDataStore().data.collectAsState(
                initial = VoiceProfile()
            ).value.voiceProfileMap.keys.forEach {
                NavTile(title = it) {

                }
            }
        }
    }
}