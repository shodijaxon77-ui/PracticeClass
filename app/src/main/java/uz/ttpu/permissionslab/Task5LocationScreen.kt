package uz.ttpu.permissionslab

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

enum class LocationPermissionState {
    PRECISE,
    APPROXIMATE,
    NONE
}

@Composable
fun Task5LocationScreen() {
    val context = LocalContext.current
    var locationState by remember { mutableStateOf(getGeneralLocationState(context)) }

    fun refreshLocationState() {
        locationState = getGeneralLocationState(context)
    }

    // Re-check permission state every time screen resumes (ON_RESUME)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        refreshLocationState()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val fineGranted = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        locationState = when {
            fineGranted -> LocationPermissionState.PRECISE
            coarseGranted -> LocationPermissionState.APPROXIMATE
            else -> LocationPermissionState.NONE
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Task 5: One-Time Location Permission",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Permissions: ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        val (cardBg, titleColor, titleText, descText) = when (locationState) {
            LocationPermissionState.PRECISE -> Quadruple(
                Color(0xFFE8F5E9),
                Color(0xFF2E7D32),
                "Precise Location Granted 🎯",
                "Full precision location is active. High accuracy GPS tracking enabled."
            )

            LocationPermissionState.APPROXIMATE -> Quadruple(
                Color(0xFFFFF3E0),
                Color(0xFFE65100),
                "Approximate Location Granted 🛰️",
                "Coarse location active (city/neighborhood level accuracy)."
            )

            LocationPermissionState.NONE -> Quadruple(
                Color(0xFFFFEBEE),
                Color(0xFFC62828),
                "Location Denied / Revoked ❌",
                "No location permission granted (or 'Only this time' permission expired)."
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = titleText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = descText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Request Location Permission")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "One-Time Permission Test Guide:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1. Tap Request and choose 'Only this time'.\n" +
                            "2. Press Home or send app to background for a few seconds.\n" +
                            "3. Re-open app: ON_RESUME lifecycle handler re-checks status and automatically updates state to 'Location Denied / Revoked'!",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun getGeneralLocationState(context: Context): LocationPermissionState {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return when {
        fineGranted -> LocationPermissionState.PRECISE
        coarseGranted -> LocationPermissionState.APPROXIMATE
        else -> LocationPermissionState.NONE
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun Task5LocationScreenPreview() {
    uz.ttpu.permissionslab.ui.theme.PracticeClassWorkTheme {
        Task5LocationScreen()
    }
}
