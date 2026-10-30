package com.example.cyberpunktcg.ui.scanner

import android.util.Log
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.cyberpunktcg.R

internal enum class CameraPreviewError { NO_REAR_CAMERA, UNAVAILABLE }

@Composable
internal fun CameraPreview(onError: (CameraPreviewError) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val reportError by rememberUpdatedState(onError)
    val previewDescription = stringResource(R.string.camera_preview)
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
            contentDescription = previewDescription
        }
    }
    var isStreaming by remember(previewView) { mutableStateOf(false) }

    DisposableEffect(previewView, lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val preview = Preview.Builder().build()
        var provider: ProcessCameraProvider? = null
        var cameraInfo: CameraInfo? = null
        var disposed = false
        val streamObserver = Observer<PreviewView.StreamState> { state ->
            isStreaming = state == PreviewView.StreamState.STREAMING
        }
        val errorObserver = Observer<CameraState> { state ->
            if (!disposed && state.error != null) reportError(CameraPreviewError.UNAVAILABLE)
        }
        previewView.previewStreamState.observe(lifecycleOwner, streamObserver)
        providerFuture.addListener({
            // Initialization may finish after navigation has already removed this screen.
            if (!disposed) {
                try {
                    val cameraProvider = providerFuture.get()
                    provider = cameraProvider
                    val selector = CameraSelector.DEFAULT_BACK_CAMERA
                    if (cameraProvider.hasCamera(selector)) {
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val camera = cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
                        cameraInfo = camera.cameraInfo
                        camera.cameraInfo.cameraState.observe(lifecycleOwner, errorObserver)
                    } else {
                        reportError(CameraPreviewError.NO_REAR_CAMERA)
                    }
                } catch (error: Exception) {
                    Log.w("CameraPreview", "Unable to open the rear camera", error)
                    reportError(CameraPreviewError.UNAVAILABLE)
                }
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose {
            disposed = true
            previewView.previewStreamState.removeObserver(streamObserver)
            cameraInfo?.cameraState?.removeObserver(errorObserver)
            // Release only this screen's preview, leaving future camera use cases independent.
            provider?.unbind(preview)
        }
    }

    Box(modifier = modifier.background(Color.Black), contentAlignment = Alignment.Center) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        if (!isStreaming) CircularProgressIndicator()
    }
}
