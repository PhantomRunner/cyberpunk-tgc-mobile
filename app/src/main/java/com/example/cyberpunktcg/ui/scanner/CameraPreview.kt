package com.example.cyberpunktcg.ui.scanner

import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.cyberpunktcg.R
import kotlinx.coroutines.launch

internal enum class CameraPreviewError { NO_REAR_CAMERA, UNAVAILABLE }

@Composable
internal fun CameraPreview(
    viewModel: ScannerViewModel,
    onError: (CameraPreviewError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val reportError by rememberUpdatedState(onError)
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()
    val previewDescription = stringResource(R.string.camera_preview)
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
            contentDescription = previewDescription
        }
    }
    val imageCapture = remember(previewView) {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }
    var isStreaming by remember(previewView) { mutableStateOf(false) }
    var isBound by remember(previewView) { mutableStateOf(false) }

    DisposableEffect(previewView, lifecycleOwner, viewModel) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val preview = Preview.Builder().build()
        var provider: ProcessCameraProvider? = null
        var cameraInfo: CameraInfo? = null
        var disposed = false
        val streamObserver = Observer<PreviewView.StreamState> { state ->
            isStreaming = state == PreviewView.StreamState.STREAMING
        }
        val errorObserver = Observer<CameraState> { state ->
            if (!disposed && state.error != null) {
                viewModel.cancelPendingCapture()
                reportError(CameraPreviewError.UNAVAILABLE)
            }
        }
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) viewModel.cancelPendingCapture()
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        previewView.previewStreamState.observe(lifecycleOwner, streamObserver)
        providerFuture.addListener({

            if (!disposed) {
                try {
                    val cameraProvider = providerFuture.get()
                    provider = cameraProvider
                    val selector = CameraSelector.DEFAULT_BACK_CAMERA
                    if (cameraProvider.hasCamera(selector)) {
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val camera = cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
                        isBound = true
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
            isBound = false
            viewModel.cancelPendingCapture()
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            previewView.previewStreamState.removeObserver(streamObserver)
            cameraInfo?.cameraState?.removeObserver(errorObserver)
            provider?.unbind(preview, imageCapture)
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().clipToBounds().background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
            if (!isStreaming) CircularProgressIndicator()
        }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.hasCaptureError) Text(stringResource(R.string.camera_capture_failed))
            Button(
                enabled = isBound && isStreaming && !state.isPreparing && !state.isCapturing,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        val request = viewModel.beginCapture() ?: return@launch
                        if (!isBound || !isStreaming ||
                            !lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                        ) {
                            viewModel.cancelPendingCapture()
                            return@launch
                        }
                        imageCapture.targetRotation = previewView.display?.rotation ?: Surface.ROTATION_0
                        try {
                            imageCapture.takePicture(
                                ImageCapture.OutputFileOptions.Builder(request.file).build(),
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        viewModel.photoCaptured(request)
                                    }
                                    override fun onError(error: ImageCaptureException) {
                                        Log.w("CameraPreview", "Unable to capture photograph", error)
                                        viewModel.captureFailed(request)
                                    }
                                },
                            )
                        } catch (error: Exception) {
                            Log.w("CameraPreview", "Unable to start image capture", error)
                            viewModel.captureFailed(request)
                        }
                    }
                },
            ) {
                if (state.isCapturing) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 12.dp).size(20.dp), strokeWidth = 2.dp)
                }
                Text(stringResource(if (state.isCapturing) R.string.camera_taking_photo else R.string.camera_take_photo))
            }
        }
    }
}
