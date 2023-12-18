package com.clementcorporation.levosonusii.presentation.loading

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.android.volley.toolbox.Volley
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoadingScreen(navController: NavController, fusedLocationClient: FusedLocationProviderClient) {
    val viewModel: LoadingScreenViewModel = hiltViewModel()
    val uiState = viewModel.loadingScreenUiState.collectAsStateWithLifecycle().value
    var address by remember { mutableStateOf("") }
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            var latitude = 0.0
            var longitude = 0.0
            location?.latitude?.let { latitude = it }
            location?.longitude?.let { longitude = it }
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= 33) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    address = addresses.first().getAddressLine(0).toString()
                }
            } else {
                val addressFromGeocoder =
                    geocoder.getFromLocation(latitude, longitude, 1)?.first()
                address = addressFromGeocoder?.getAddressLine(0).toString()
            }
            if (address.isEmpty()) {
                val queue = Volley.newRequestQueue(context)
                viewModel.getAddressWhenGeocoderOffline(
                    queue,
                    latitude.toString(),
                    longitude.toString()
                )
            } else {
                viewModel.getBusinessByAddress(address)
            }
        }
    } else {
        LaunchedEffect(key1 = !permissionState.hasPermission) {
            permissionState.launchPermissionRequest()
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        elevation = ELEVATION.dp,
        color = Color.White,
        shape = RoundedCornerShape(CURVATURE.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is LoadingScreenUiState.OnLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = Color.Blue
                    )
                }
                is LoadingScreenUiState.OnFetchUsersBusiness -> {
                    SideEffect {
                        navController.navigate(LevoSonusScreens.LoginScreen.name)
                        Toast.makeText(
                            context,
                            context.getString(R.string.organization_name_success_toast_message, uiState.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is LoadingScreenUiState.OnFailedToRetrieveBusiness -> {
                    SideEffect {
                        Toast.makeText(
                            context,
                            context.getString(R.string.organization_name_failed_toast_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
