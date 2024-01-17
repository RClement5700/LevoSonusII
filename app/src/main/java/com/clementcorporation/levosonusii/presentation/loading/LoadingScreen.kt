package com.clementcorporation.levosonusii.presentation.loading

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavController
import com.android.volley.toolbox.Volley
import com.clementcorporation.levosonusii.R
import com.clementcorporation.levosonusii.util.Constants.CURVATURE
import com.clementcorporation.levosonusii.util.Constants.ELEVATION
import com.clementcorporation.levosonusii.util.LevoSonusScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoadingScreen(navController: NavController, fusedLocationClient: FusedLocationProviderClient) {
    val viewModel: LoadingScreenViewModel = hiltViewModel()
    val uiState = viewModel.loadingScreenUiState.collectAsStateWithLifecycle().value
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val scope = LocalLifecycleOwner.current.lifecycle.coroutineScope
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
                    viewModel.address = addresses.first().getAddressLine(0).toString()
                }
            } else {
                val addressFromGeocoder =
                    geocoder.getFromLocation(latitude, longitude, 1)?.first()
                viewModel.address = addressFromGeocoder?.getAddressLine(0).toString()
            }
            if (viewModel.address.isEmpty()) {
                val queue = Volley.newRequestQueue(context)
                viewModel.getAddressWhenGeocoderOffline(
                    queue,
                    latitude.toString(),
                    longitude.toString()
                )
            } else {
                viewModel.getBusinessByAddress()
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    Snackbar(
                        shape = RoundedCornerShape(8.dp),
                        elevation = 4.dp
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.organization_name_success_snackbar_message, uiState.name),
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                    SideEffect {
                        scope.launch {
                            navController.navigate(LevoSonusScreens.LoginScreen.name)
                        }
                    }
                }
                is LoadingScreenUiState.OnFailedToRetrieveBusiness -> {
                    Snackbar(
                        shape = RoundedCornerShape(8.dp),
                        elevation = 4.dp,
                        actionOnNewLine = true,
                        action = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.getBusinessByAddress()
                                        },
                                    text = stringResource(id = R.string.organization_name_failed_snackbar_action),
                                    textAlign = TextAlign.Center,
                                    color = Color.LightGray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    modifier = Modifier.size(24.dp),
                                    onClick = { viewModel.getBusinessByAddress()}) {
                                        Icon(
                                            imageVector = Icons.Outlined.Refresh,
                                            contentDescription = "Refresh Loading Screen"
                                        )
                                    }
                            }
                        }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.organization_name_failed_snackbar_message),
                            textAlign = TextAlign.Center,
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}
