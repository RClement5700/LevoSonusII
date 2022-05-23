package com.clementcorporation.levosonusii.screens.voiceprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.model.LSUserInfo
import com.google.firebase.firestore.FirebaseFirestore

/*
    TODO:
        -pass employeeId from dataStore into db
        -display employeeId, email & name to user
        -request that they take a screen shot or write it down
        -notify them that it can be emailed to them should they forget their password
        -request to create a voice profile now or later (positive/negative buttons)
        -on positive pushed: navController.navigate(LevoSonusScreens.VoiceProfileScreen.name)
        -on negative pushed: navController.navigate(LevoSonusScreens.HomeScreen.name)
 */
@Composable
fun CreateVoiceProfileScreen(navController: NavController) {
    val viewModel: CreateVoiceProfileViewModel = hiltViewModel()

    Card(
        elevation = Constants.ELEVATION.dp,
        shape = RoundedCornerShape(Constants.CURVATURE.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(PADDING.dp)
            .fillMaxSize(.5f)
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(Constants.CURVATURE.dp))
                .padding(PADDING.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(PADDING.dp),
                text = "Employee ID: ${viewModel.getDataStore().data.collectAsState(initial = LSUserInfo()).value.employeeId}",
                color = Color.Gray
            )
            Text(
                modifier = Modifier.padding(PADDING.dp),
                text = "Email Address: ${viewModel.getDataStore().data.collectAsState(initial = LSUserInfo()).value.emailAddress}",
                color = Color.Gray
            )
//            FirebaseFirestore.getInstance().collection("users").document(
//                viewModel.getDataStore().data.collectAsState(initial = LSUserInfo()).value.employeeId
//            )
        }
    }
}