package uz.ttpu.permissionslab

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
fun Task3CameraScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    var isGranted by remember { mutableStateOf(checkCameraPermission(context)) }
    var shouldShowRationale by remember {
        mutableStateOf(activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
        } ?: false)
    }
    var denialCount by remember { mutableIntStateOf(0) }

    fun refreshState() {
        isGranted = checkCameraPermission(context)
        shouldShowRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
        } ?: false
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        refreshState()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        if (!granted) {
            denialCount++
            shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } ?: false
        } else {
            denialCount = 0
            shouldShowRationale = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Task 3: Camera Runtime Permission",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SDK Version < 23 (API 23 lower)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text("Permission is granted automatically at install time on this device.")
                }
            }
            return
        }

        // Branch 1: Permission Granted
        if (isGranted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "State 1: Granted 📷",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Camera permission has been granted! Viewfinder active.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFF81C784), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Live Camera Preview Frame",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        // Branch 2: Rationale Shown
        else if (shouldShowRationale) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "State 2: Rationale Required ⚠️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Why we need the camera:\nThis app requires access to your camera to scan QR codes and capture photo attachments for your classwork.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { launcher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Camera Permission")
                    }
                }
            }
        }
        // Branch 3: No Permission / Initial State or Permanently Denied
        else {
            val isPermanentlyDenied = denialCount > 0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPermanentlyDenied) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isPermanentlyDenied) "State 3: Permanently Denied (2x) 🚫" else "State 3: Permission Not Granted ❓",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPermanentlyDenied) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isPermanentlyDenied)
                            "You have denied the camera permission twice. Android blocks further dialog prompts. Please open App Settings to grant permission manually."
                        else
                            "Camera access is not granted yet. Tap below to request permission.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isPermanentlyDenied) {
                        Button(
                            onClick = { openAppSettings(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open App Settings")
                        }
                    } else {
                        Button(
                            onClick = { launcher.launch(Manifest.permission.CAMERA) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Request Camera Permission")
                        }
                    }
                }
            }
        }
    }
}

private fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun Task3CameraScreenPreview() {
    uz.ttpu.permissionslab.ui.theme.PracticeClassWorkTheme {
        Task3CameraScreen()
    }
}
