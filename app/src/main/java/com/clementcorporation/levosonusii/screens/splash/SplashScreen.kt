package com.clementcorporation.levosonusii.screens.splash

import android.content.res.Configuration
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.clementcorporation.levosonusii.main.Constants.CURVATURE
import com.clementcorporation.levosonusii.main.Constants.ELEVATION
import com.clementcorporation.levosonusii.main.Constants.PADDING
import com.clementcorporation.levosonusii.main.LevoSonusLogo
import com.clementcorporation.levosonusii.main.MainActivity
import com.clementcorporation.levosonusii.main.MainActivityViewModel
import com.clementcorporation.levosonusii.navigation.LevoSonusScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
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
            if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
                navController.navigate(LevoSonusScreens.LoginScreen.name)
            } else {
                navController.navigate(LevoSonusScreens.HomeScreen.name)
            }
        }
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                SplashScreenContent(scale = scale, paddingStartEnd = 175, paddingTopBottom = 16)
            }
            else -> {
                SplashScreenContent(scale = scale, paddingStartEnd = 16, paddingTopBottom = 175)
            }
        }
    }
}

@Composable
fun SplashScreenContent(scale: Animatable<Float, AnimationVector1D>, paddingStartEnd: Int, paddingTopBottom: Int) {
    Card(
        modifier = Modifier
            .scale(scale.value)
            .padding(
                top = paddingTopBottom.dp, bottom = paddingTopBottom.dp,
                start = paddingStartEnd.dp, end = paddingStartEnd.dp
            ),
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
