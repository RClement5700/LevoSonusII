package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
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
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Voice Profile",
                modifier = Modifier.padding(8.dp),
                color = LS_BLUE,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = LS_BLUE, thickness = 2.dp, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            viewModel.getVoiceProfileDataStore().data.collectAsState(
                initial = VoiceProfile()
            ).value.voiceProfileMap.keys.forEach {
                NavTile(title = it) {

                }
            }
        }
    }
}