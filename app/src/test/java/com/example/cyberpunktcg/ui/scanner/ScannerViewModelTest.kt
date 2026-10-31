package com.example.cyberpunktcg.ui.scanner

import androidx.lifecycle.ViewModelStore
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ScannerViewModelTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun onlyOneCaptureIsAllowedUntilRetake() = runBlocking {
        val viewModel = preparedViewModel()
        val request = viewModel.beginCapture()!!
        assertTrue(viewModel.uiState.isCapturing)
        assertNull(viewModel.beginCapture())

        viewModel.photoCaptured(request)
        assertEquals(request.file, viewModel.uiState.photoFile)
        assertFalse(viewModel.uiState.isCapturing)
        assertNull(viewModel.beginCapture())

        viewModel.reset()
        awaitDeletion(request.file)
        assertNull(viewModel.uiState.photoFile)
        assertNotNull(viewModel.beginCapture())
        viewModel.reset()
    }

    @Test
    fun failureRemovesPartialFileAndAllowsAnotherAttempt() = runBlocking {
        val viewModel = preparedViewModel()
        val request = viewModel.beginCapture()!!
        request.file.writeText("unfinished JPEG")
        viewModel.captureFailed(request)
        assertFalse(viewModel.uiState.isCapturing)
        assertTrue(viewModel.uiState.hasCaptureError)
        assertNull(viewModel.uiState.photoFile)
        awaitDeletion(request.file)

        assertNotNull(viewModel.beginCapture())
        assertFalse(viewModel.uiState.hasCaptureError)
        viewModel.reset()
    }

    @Test
    fun resetRejectsLateCallbacksWithoutInterruptingANewerCapture() = runBlocking {
        val viewModel = preparedViewModel()
        val oldRequest = viewModel.beginCapture()!!
        viewModel.reset()
        awaitDeletion(oldRequest.file)
        val newRequest = viewModel.beginCapture()!!


        oldRequest.file.writeText("late photograph")
        viewModel.photoCaptured(oldRequest)
        viewModel.captureFailed(oldRequest)
        assertNull(viewModel.uiState.photoFile)
        assertTrue(viewModel.uiState.isCapturing)
        assertFalse(viewModel.uiState.hasCaptureError)
        awaitDeletion(oldRequest.file)

        viewModel.photoCaptured(newRequest)
        assertEquals(newRequest.file, viewModel.uiState.photoFile)
        viewModel.reset()
    }

    @Test
    fun cameraDisposalCancelsPendingCaptureButKeepsACompletedPhoto() = runBlocking {
        val viewModel = preparedViewModel()
        val cancelled = viewModel.beginCapture()!!
        viewModel.cancelPendingCapture()
        viewModel.photoCaptured(cancelled)
        assertNull(viewModel.uiState.photoFile)
        assertFalse(viewModel.uiState.isCapturing)

        val completed = viewModel.beginCapture()!!
        viewModel.photoCaptured(completed)
        viewModel.cancelPendingCapture()
        viewModel.prepare()
        assertEquals(completed.file, viewModel.uiState.photoFile)
        viewModel.reset()
    }

    @Test
    fun aNewSessionCleansOrphanedPhotographs() = runBlocking {
        val directory = temporaryFolder.newFolder()
        val orphan = File(directory, "old.jpg").apply { writeText("old photograph") }
        val viewModel = ScannerViewModel(directory)
        viewModel.prepare()
        assertFalse(orphan.exists())
        assertFalse(viewModel.uiState.isPreparing)
        assertNull(viewModel.uiState.photoFile)
    }

    @Test
    fun anUnwritablePhotoLocationReportsFailureAndCanBeRetried() = runBlocking {
        val directory = temporaryFolder.newFolder()
        val viewModel = ScannerViewModel(directory)
        viewModel.prepare()
        assertTrue(directory.delete())
        directory.writeText("a file cannot contain photographs")
        assertNull(viewModel.beginCapture())
        assertFalse(viewModel.uiState.isCapturing)
        assertTrue(viewModel.uiState.hasCaptureError)

        assertTrue(directory.delete())
        assertNotNull(viewModel.beginCapture())
        viewModel.reset()
    }

    @Test
    fun aClearedNavigationEntryCannotRestoreALatePhoto() = runBlocking {
        val viewModel = preparedViewModel()
        val store = ViewModelStore().apply { put("scanner", viewModel) }
        val request = viewModel.beginCapture()!!
        store.clear()
        awaitDeletion(request.file)
        request.file.writeText("late photograph")
        viewModel.photoCaptured(request)
        assertNull(viewModel.uiState.photoFile)
        assertNull(viewModel.beginCapture())
        awaitDeletion(request.file)
    }

    private suspend fun preparedViewModel(): ScannerViewModel {
        return ScannerViewModel(temporaryFolder.newFolder()).also { it.prepare() }
    }

    private suspend fun awaitDeletion(file: File) {
        withTimeout(5_000) { while (file.exists()) delay(10) }
    }
}
