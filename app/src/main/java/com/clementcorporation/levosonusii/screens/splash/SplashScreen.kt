package com.clementcorporation.levosonusii.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import kotlinx.coroutines.delay

private val ELEVATION = 8.dp
private val CURVATURE = 16.dp
private val PADDING = 8.dp
@Composable
fun SplashScreen(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING),
        elevation = ELEVATION,
        color = Color.White,
        shape = RoundedCornerShape(CURVATURE)
    ) {
        val scale = remember {
            Animatable(0f)
        }
        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.9f,
                animationSpec = tween(durationMillis = 800,
                    easing = {
                        OvershootInterpolator(8f)
                            .getInterpolation(it)
                    })
            )
            delay(2000L)

//            if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
//                navController.navigate(ReaderScreens.LoginScreen.name)
//            }else {
//                navController.navigate(ReaderScreens.ReaderHomeScreen.name)
//            }


        }

        Card(
            modifier = Modifier
                .scale(scale.value)
                .padding(start = 150.dp, end = 150.dp),
            shape = CircleShape,
            elevation = ELEVATION,
            backgroundColor = Color.LightGray,
            border = BorderStroke(
                width = 2.dp,
                color = Color.LightGray
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LevoSonusLogo()
            }
        }
    }
}
