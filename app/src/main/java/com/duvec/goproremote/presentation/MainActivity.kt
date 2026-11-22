/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.duvec.goproremote.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.duvec.goproremote.R
import com.duvec.goproremote.presentation.theme.GoProRemoteTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.collectAsState
import com.duvec.goproremote.ble.GoProCamera

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp("Charles")
        }
    }
}

@Composable
fun WearApp(greetingName: String) {

    var isRecording by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0)}

    LaunchedEffect(isRecording) {
        if (isRecording){
            seconds = 0
            while (true) {
                delay(1000)
                seconds++
            }
        }
    }

    val context = LocalContext.current
    val blePermissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    var hasBlePermission by remember {
        mutableStateOf(
            blePermissions.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) ==
                        PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasBlePermission = results.values.all { it }
    }

    val camera = remember{ GoProCamera(context) }
    val cameraStatus by camera.status.collectAsState()

    GoProRemoteTheme {
        AppScaffold {
            ScreenScaffold { contentPadding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 1. A Text showing "Battery 87%"
                    // 2. A Button labelled "Record" (copy the Button A pattern)
                    // 3. A Text showing "00:00"
                    if (hasBlePermission) {
                        if (cameraStatus == "Connected") {
                            Button(onClick = { camera.disconnect() }) {
                                Text("Disconnect")
                            }
                        } else {
                            Button(onClick = { camera.startScan() }) {
                                Text("Find GoPro")
                            }
                        }
                        Text(cameraStatus)
                    } else {
                        Button(onClick = { permissionLauncher.launch(blePermissions) }) {
                            Text("Allow Bluetooth")
                        }
                    }
                    //Text("Battery 87%")

                    Button(
                        onClick = { isRecording = !isRecording },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = if (isRecording) Color.White else Color.Black,
                            containerColor = if(isRecording) Color.Red else MaterialTheme.colorScheme.primary
                        )

                    ) {
                        Text(if (isRecording) "Stop" else "Record")
                    }

                    Text("%02d:%02d".format(seconds / 60,seconds % 60))
                }
            }
        }
    }
}
//fun WearApp(greetingName: String) {
//    GoProRemoteTheme {
//        AppScaffold {
//            val listState = rememberTransformingLazyColumnState()
//            val transformationSpec = rememberTransformationSpec()
//            ScreenScaffold(
//                scrollState = listState,
//                edgeButton = {
//                    EdgeButton(
//                        onClick = { /*TODO*/ },
//                        colors =
//                            ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
//                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//                            ),
//                    ) {
//                        Text("More")
//                    }
//                },
//            ) { contentPadding -> // ScreenScaffold provides default padding; adjust as needed
//                TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
//                    item {
//                        ListHeader(
//                            modifier =
//                                Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
//                            transformation = SurfaceTransformation(transformationSpec),
//                        ) {
//                            Text(text = stringResource(R.string.hello_world, greetingName))
//                        }
//                    }
//                    item {
//                        Button(
//                            onClick = { /*TODO*/ },
//                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
//                            transformation = SurfaceTransformation(transformationSpec),
//                        ) {
//                            Text("Button A")
//                        }
//                    }
//                    item {
//                        Button(
//                            onClick = { /*TODO*/ },
//                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
//                            transformation = SurfaceTransformation(transformationSpec),
//                        ) {
//                            Text("Button B")
//                        }
//                    }
//                    item {
//                        Button(
//                            onClick = { /*TODO*/ },
//                            modifier = Modifier.fillMaxWidth().transformedHeight(this, transformationSpec),
//                            transformation = SurfaceTransformation(transformationSpec),
//                        ) {
//                            Text("Button C")
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp("Charles")
}