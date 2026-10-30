package com.example.cyberpunktcg.ui.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.cyberpunktcg.R

@Composable
internal fun ScannerScreen() {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    fun hasCameraPermission(): Boolean = ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA,
    ) == PackageManager.PERMISSION_GRANTED

    var permissionGranted by remember { mutableStateOf(hasCameraPermission()) }
    var initialRequestMade by rememberSaveable { mutableStateOf(false) }
    var needsSettings by rememberSaveable { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<CameraPreviewError?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionGranted = granted
        needsSettings = !granted && activity != null &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
        cameraError = null
    }

    LaunchedEffect(Unit) {
        if (!initialRequestMade) {
            initialRequestMade = true
            if (!permissionGranted) permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    DisposableEffect(lifecycleOwner, context) {
        // Permission may change while the user is in the Android app settings.
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val granted = hasCameraPermission()
                if (granted && !permissionGranted) cameraError = null
                if (granted) needsSettings = false
                permissionGranted = granted
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when {
        !permissionGranted -> {
            ScannerMessage(
                messageResId = R.string.camera_permission_message,
                actionResId = if (needsSettings) R.string.camera_open_settings else R.string.camera_grant_access,
                onAction = {
                    if (needsSettings) {
                        context.startActivity(Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null),
                        ))
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
            )
        }
        cameraError != null -> ScannerMessage(
            messageResId = if (cameraError == CameraPreviewError.NO_REAR_CAMERA) {
                R.string.camera_no_rear_camera
            } else R.string.camera_unavailable,
            actionResId = R.string.camera_retry,
            onAction = { cameraError = null },
        )
        else -> CameraPreview(onError = { cameraError = it }, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun ScannerMessage(@StringRes messageResId: Int, @StringRes actionResId: Int, onAction: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(messageResId), textAlign = TextAlign.Center)
            Button(onClick = onAction) { Text(stringResource(actionResId)) }
        }
    }
}
