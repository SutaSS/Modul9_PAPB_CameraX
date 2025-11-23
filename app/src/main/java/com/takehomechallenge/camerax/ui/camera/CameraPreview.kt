package com.takehomechallenge.camerax.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var mediaPermissionGranted by remember { mutableStateOf(false) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCameraReady by remember { mutableStateOf(false) }

    val launcherCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> cameraPermissionGranted = granted }

    val launcherMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> mediaPermissionGranted = granted }

    LaunchedEffect(Unit) {
        launcherCamera.launch(Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcherMedia.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            launcherMedia.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    Box(Modifier.fillMaxSize()) {

        if (cameraPermissionGranted) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }.also { previewView = it }
                }
            )

            LaunchedEffect(previewView) {
                val pv = previewView ?: return@LaunchedEffect
                pv.doOnLayout {
                    try {
                        val capture = bindWithImageCapture(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            previewView = pv
                        )

                        imageCapture = capture
                        isCameraReady = true

                    } catch (e: Exception) {
                        Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            PermissionDeniedUI(
                onRetry = { launcherCamera.launch(Manifest.permission.CAMERA) }
            )
        }


        IconButton(
            onClick = {
                if (isCameraReady) {
                    imageCapture?.let { ic ->
                        takePhoto(context, ic) { uri ->
                            Toast.makeText(context, "Foto tersimpan: $uri", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            enabled = isCameraReady
        ) {
            Icon(
                Icons.Default.AddCircle,
                contentDescription = "Take Photo",
                modifier = Modifier.size(84.dp)
            )
        }
    }
}



 fun bindWithImageCapture(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView
): ImageCapture {

    val cameraProvider = ProcessCameraProvider.getInstance(context).get()

    val rotation = previewView.display?.rotation ?: Surface.ROTATION_0

    val preview = Preview.Builder()
        .setTargetRotation(rotation)
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .setTargetRotation(rotation)
        .setJpegQuality(95)
        .build()

    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        CameraSelector.DEFAULT_BACK_CAMERA,
        preview,
        imageCapture
    )

    return imageCapture
}



fun outputOptions(ctx: Context): ImageCapture.OutputFileOptions {
    val name = "IMG_${System.currentTimeMillis()}.jpg"

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/KameraKu")
        }
    }

    return ImageCapture.OutputFileOptions.Builder(
        ctx.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    ).build()
}



fun takePhoto(
    ctx: Context,
    ic: ImageCapture,
    onSaved: (Uri) -> Unit
) {
    val opts = outputOptions(ctx)

    ic.takePicture(
        opts,
        ContextCompat.getMainExecutor(ctx),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                result.savedUri?.let(onSaved)
            }

            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(ctx, "Error: ${exc.message}", Toast.LENGTH_LONG).show()
            }
        }
    )
}

@Composable
fun PermissionDeniedUI(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Izin kamera diperlukan",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Aplikasi membutuhkan akses kamera untuk mengambil foto. " +
                            "Silakan izinkan akses kamera agar fitur dapat berjalan.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(20.dp))
                Button(onClick = onRetry) {
                    Text("Izinkan Kamera")
                }
            }
        }
    }
}

