package com.clementcorporation.levosonusii.screens.departments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.LS_BLUE
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.NavTile

@Composable
fun DepartmentsScreen(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxSize().padding(PADDING.dp),
        elevation = ELEVATION.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Departments",
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
            NavTile(title = "Grocery", icon = R.drawable.grocery_icon, showIcon = remember{mutableStateOf(true)})
            NavTile(title = "Meat", icon = R.drawable.meat_icon1, showIcon = remember{mutableStateOf(true)})
            NavTile(title = "Seafood", icon = R.drawable.seafood_icon, showIcon = remember{mutableStateOf(true)})
            NavTile(title = "Dairy", icon = R.drawable.dairy_icon, showIcon = remember{mutableStateOf(true)})
            NavTile(title = "Produce", icon = R.drawable.produce_icon, showIcon = remember{mutableStateOf(true)})
            NavTile(title = "Freezer", icon = R.drawable.freezer_icon, showIcon = remember{mutableStateOf(true)})
            NavTile(title = "Miscellaneous", icon = R.drawable.misc_icon, showIcon = remember{mutableStateOf(true)})
        }
    }
}