package com.kireaji.nfcsampleapp.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ReaderCallback
import android.nfc.Tag
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.kireaji.nfcsampleapp.MainActivity
import com.kireaji.nfcsampleapp.MainActivity.Companion.TAG
import com.kireaji.nfcsampleapp.ui.theme.NfcSampleTheme
import com.kireaji.nfcsampleapp.ui.viewmodel.NfcScanViewModel
import com.kireaji.nfcsampleapp.ui.viewmodel.NfcScanViewModelImpl
import com.kireaji.nfcsampleapp.ui.viewmodel.NfcState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.StringBuilder
import java.util.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NfcScanScreen(viewModel: NfcScanViewModel) {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val nfcState by viewModel.stateNfc.collectAsState()
    var id by rememberSaveable { mutableStateOf("") }

    when (nfcState) {
        is NfcState.None -> {}
        is NfcState.Ready -> {}
        is NfcState.Success -> {
            id = (nfcState as NfcState.Success).id
        }
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "スキャンの準備が出来ました")

                Box(
                    modifier = Modifier.size(56.dp)
                )

                Button(
                    onClick = {
                        viewModel.onClickCancelScan()
                        scope.launch { state.hide() }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "キャンセル",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "NfcCheck")
                    }
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(it),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResultId(id = id, onIdChange = {})

                Button(
                    onClick = {
                        id = ""
                        viewModel.onClickStartScan()
                        scope.launch {
                            state.show()
                        }
                    },
                    modifier = Modifier.width(300.dp)
                ) {
                    Text(
                        "NFCスキャン",
                        modifier = Modifier.padding(24.dp)
                    )

                }

            }
        }
    }
}

@Composable
fun ResultId(id: String, onIdChange: (String) -> Unit) {
    OutlinedTextField(
        value = id,
        onValueChange = onIdChange,
        label = { Text("Read ID ..") },
        readOnly = true
    )
}

fun bytesToHexString(bytes: ByteArray): String {
    val sb = StringBuilder()
    val formatter = Formatter(sb)
    for (b in bytes) {
        formatter.format("%02x", b)
    }
    return sb.toString().uppercase(Locale.getDefault())
}


@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NfcSampleTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            NfcScanScreen(object : NfcScanViewModel {
                override val eventStartScan: SharedFlow<Unit>
                    get() = TODO("Not yet implemented")
                override val eventCancelScan: SharedFlow<Unit>
                    get() = TODO("Not yet implemented")
                override val stateNfc: StateFlow<NfcState>
                    get() = TODO("Not yet implemented")

                override fun onClickStartScan() {
                    TODO("Not yet implemented")
                }

                override fun onClickCancelScan() {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}