package com.clementcorporation.levosonusii.screens.equipment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.main.Constants
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.NavTile

@Composable
fun EquipmentScreen(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(Constants.PADDING.dp),
        elevation = Constants.ELEVATION.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(Constants.CURVATURE.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Equipment",
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
            NavTile(title = "Headset", icon = R.drawable.headset_icon, showIcon = remember {
                mutableStateOf(true)
            })
            NavTile(title = "Scanner", icon = R.drawable.scanner_icon, showIcon = remember {
                mutableStateOf(true)
            })
            NavTile(title = "Machine", icon = R.drawable.forklift_icon, showIcon = remember {
                mutableStateOf(true)
            })
        }
    }
}