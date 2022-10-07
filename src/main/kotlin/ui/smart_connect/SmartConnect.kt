@file:OptIn(ExperimentalMaterialApi::class)

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ui.smart_connect.*
import javax.swing.JOptionPane.showInputDialog

fun main() = application {
    val applicationState = remember { MyApplicationState() }

    for (window in applicationState.windows) {
        key(window) {
            ShowDeviceWindow(window)
        }
    }
}

@Preview
@Composable
fun ShowDeviceWindow(window: MyWindowState) {
    var devicesList: MutableList<NetworkDevices> by remember { mutableStateOf(mutableListOf()) }
    val windowState = WindowState(
        size = DpSize(500.dp, 500.dp),
    )
    Window(
        state = windowState,
        onCloseRequest = window::close,
        title = "Network Devices",
    ) {
        MenuBar {
            Menu("Action") {
                Item("Refresh", onClick = {
                    devicesList = getDeviceList()
                })
            }
        }
        Column() {
            LazyColumn(
                modifier = Modifier
                    .padding(5.dp)
                    .border(BorderStroke(1.dp, Color(Color.Gray.value)))
            ) {
                items(items = devicesList,
                    key = {
                        it.hashCode()
                    }) { device ->
                    Row(
                        Modifier.padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 6.dp),
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically).weight(1f),
                            text = device.mac ?: "",
                            style = textViewStyle,
                        )
                        Text(
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically).weight(1f),
                            text = device.ip ?: "",
                            style = textViewStyle
                        )
                        Button(
                            modifier = Modifier.align(alignment = Alignment.CenterVertically).weight(0.4f).widthIn(0.dp, 150.dp),
                            content = {
                                Text(
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                                    text = device.name ?: "Add",
                                    style = textViewStyle
                                )
                            }, onClick = {
                                val input = showInputDialog("Device Name",device.name)
                                device.name = input
                                saveConfiguration(devicesList)
                                devicesList.clear()
                                devicesList = initList()
                            })
                    }
                    Divider()
                }
            }
        }
        devicesList = initList()
    }
}

fun initList(): MutableList<NetworkDevices> {
    val activeDeviceList = getDeviceList()
    for (loadedDevice in loadConfiguration() ?: listOf()) {
        val activeDevice = activeDeviceList.firstOrNull {
            it.mac == loadedDevice.mac
        }
        activeDevice?.name = loadedDevice.name
    }
    return activeDeviceList
}
