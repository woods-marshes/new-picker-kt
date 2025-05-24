package io.github.woods_marshes.new_packer_kt

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.woods_marshes.base.builder.PickerKt
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.new_packer_kt.ui.theme.NewPickerKtTheme
import io.github.woods_marshes.ui.common.PickerKtActivityResult

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewPickerKtTheme {
                val permissionsToRequest = remember {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                        arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO
                        )
                    } else { // Older versions
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }

                var hasPermissions by remember { mutableStateOf(false) }
                var shouldShowRationale by remember { mutableStateOf(false) }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionsResultMap: Map<String, Boolean> ->
                    hasPermissions = permissionsResultMap.values.all { it }
                    shouldShowRationale = permissionsResultMap.entries.any { !it.value && !shouldShowRequestPermissionRationale(it.key) }
                }

                val imagesReturnedFromPickerKt = remember { mutableStateListOf<Uri>() }
                val pickerLauncher =
                    rememberLauncherForActivityResult(contract = PickerKtActivityResult()) {
                        imagesReturnedFromPickerKt.addAll(it)
                    }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (imagesReturnedFromPickerKt.isNotEmpty()) {
                            FloatingActionButton(onClick = { imagesReturnedFromPickerKt.clear() }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Clear selected images"
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "PickerKT Sample",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "There are ${imagesReturnedFromPickerKt.size} selected images.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    textAlign = TextAlign.Center
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { permissionLauncher.launch(permissionsToRequest) },
                                        enabled = !hasPermissions
                                    ) {
                                        Text(text = if (hasPermissions) "Permission Granted" else "Grant Permission")
                                    }

                                    Button(
                                        enabled = hasPermissions,
                                        onClick = {
                                            pickerLauncher.launch(
                                                PickerKt.picker {
                                                    allowMimes {
                                                        add { MimeType.Jpeg }
                                                        add { MimeType.Png }
                                                        add { MimeType.Gif }
                                                        add { MimeType.Svg }
                                                        add { MimeType.Mpeg4 }
                                                        add { MimeType.MsWordDoc2007 }
                                                        add { MimeType.Mp3 }
                                                        add { MimeType.OggAudio }
                                                    }

                                                    selection {
                                                        maxSelection(25)
                                                    }
                                                }
                                            )
                                        }
                                    ) {
                                        Text(text = "Open Picker")
                                    }
                                }

                                if (shouldShowRationale && !hasPermissions) {
                                    Text(text = "Allow 'Read External Storage permission' to open the picker")
                                }
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(120.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            items(imagesReturnedFromPickerKt) {
                                val painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current).data(it).crossfade(true).build()
                                )
                                Column(
                                    modifier = Modifier
                                        .animateItem()
                                        .fillMaxWidth()
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(it),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .fillMaxWidth(),
                                        alignment = Alignment.Center,
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(text = it.toString(), maxLines = 3)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}