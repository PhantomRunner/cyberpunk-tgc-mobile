package com.example.cyberpunktcg.ui.scanner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal data class ScannerUiState(
    val photoFile: File? = null,
    val isPreparing: Boolean = true,
    val isCapturing: Boolean = false,
    val hasCaptureError: Boolean = false,
)

internal data class PhotoCaptureRequest(val id: Long, val file: File)

internal class ScannerViewModel(private val photoDirectory: File) : ViewModel() {
    var uiState by mutableStateOf(ScannerUiState())
        private set

    private var requestId = 0L
    private var pendingCapture: PhotoCaptureRequest? = null
    private var closed = false


    private val cachePreparation = viewModelScope.async(Dispatchers.IO) {
        try {
            ensurePhotoDirectory()
            val files = photoDirectory.listFiles() ?: throw IOException("Unable to read scanner photo directory")
            files.filter { it.isFile }.forEach { file ->
                if (!file.delete() && file.exists()) throw IOException("Unable to remove old photograph")
            }
            true
        } catch (_: IOException) {
            false
        }
    }

    suspend fun prepare() {
        if (!uiState.isPreparing) return
        val prepared = cachePreparation.await()
        if (!closed) uiState = uiState.copy(isPreparing = false, hasCaptureError = !prepared)
    }

    suspend fun beginCapture(): PhotoCaptureRequest? {
        if (closed || uiState.isPreparing || uiState.isCapturing || uiState.photoFile != null) return null
        val id = ++requestId
        uiState = uiState.copy(isCapturing = true, hasCaptureError = false)
        var file: File? = null
        try {
            withContext(Dispatchers.IO) {
                ensurePhotoDirectory()
                file = File.createTempFile("photo_", ".jpg", photoDirectory)
            }
        } catch (error: CancellationException) {
            deletePhoto(file)
            if (id == requestId) cancelPendingCapture()
            throw error
        } catch (_: IOException) {
            if (id == requestId) uiState = uiState.copy(isCapturing = false, hasCaptureError = true)
            return null
        }
        if (closed || id != requestId) {
            deletePhoto(file)
            return null
        }
        return PhotoCaptureRequest(id, checkNotNull(file)).also { pendingCapture = it }
    }

    fun photoCaptured(request: PhotoCaptureRequest) {
        if (closed || request != pendingCapture) {
            deletePhoto(request.file)
            return
        }
        pendingCapture = null
        uiState = uiState.copy(photoFile = request.file, isCapturing = false, hasCaptureError = false)
    }

    fun captureFailed(request: PhotoCaptureRequest) {
        deletePhoto(request.file)
        if (!closed && request == pendingCapture) {
            pendingCapture = null
            uiState = uiState.copy(isCapturing = false, hasCaptureError = true)
        }
    }

    fun cancelPendingCapture() {
        if (!uiState.isCapturing) return
        ++requestId
        deletePhoto(pendingCapture?.file)
        pendingCapture = null
        uiState = uiState.copy(isCapturing = false, hasCaptureError = false)
    }

    fun reset() {
        ++requestId
        deletePhoto(pendingCapture?.file)
        deletePhoto(uiState.photoFile)
        pendingCapture = null
        uiState = uiState.copy(photoFile = null, isCapturing = false, hasCaptureError = false)
    }

    override fun onCleared() {
        closed = true
        reset()
    }

    private fun ensurePhotoDirectory() {
        if (!photoDirectory.isDirectory && !photoDirectory.mkdirs()) {
            throw IOException("Unable to create scanner photo directory")
        }
    }

    private fun deletePhoto(file: File?) {

        if (file != null) Dispatchers.IO.asExecutor().execute { file.delete() }
    }
}
