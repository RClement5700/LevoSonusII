package com.clementcorporation.levosonusii.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(PADDING.dp),
        elevation = ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
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

//            if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
//                navController.navigate(LevoSonusScreens.LoginScreen.name)
//            } else {
//                navController.navigate(LevoSonusScreens.HomeScreen.name)
//            }
            FirebaseAuth.getInstance().signOut()
            navController.navigate(LevoSonusScreens.LoginScreen.name)
        }
        Card(
            modifier = Modifier
                .scale(scale.value)
                .padding(top = 175.dp, bottom = 175.dp, start = 16.dp, end = 16.dp),
            shape = CircleShape,
            elevation = ELEVATION.dp,
            backgroundColor = Color.White.copy(0.5f)
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
