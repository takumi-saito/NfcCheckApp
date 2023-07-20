package com.kireaji.nfcsampleapp

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
import com.kireaji.nfcsampleapp.MainActivity.Companion.TAG
import com.kireaji.nfcsampleapp.ui.theme.NfcSampleTheme
import java.lang.StringBuilder
import java.util.*

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        setContent {
            NfcSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NfcTest(nfcAdapter, this@MainActivity)
                }
            }
        }
    }
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NfcTest(nfcAdapter: NfcAdapter?, activity: MainActivity?) {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    var id by rememberSaveable { mutableStateOf("") }

    class MyReaderCallback : ReaderCallback {
        override fun onTagDiscovered(tag: Tag) {
            Log.d(TAG, "Tag discoverd.")

            //get idm
            val idm = tag.id
            val idmString = bytesToHexString(idm)
            Log.d(TAG, idmString)
            id = idmString
            if (id != "") {
                nfcAdapter?.disableReaderMode(activity)
                scope.launch { state.hide() }
            }
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
                        nfcAdapter?.disableReaderMode(activity)
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

                ResultId(id = id, onIdChange = { id = it })

                Button(
                    onClick = {
                        id = ""
                        nfcAdapter?.enableReaderMode(
                            activity,
                            MyReaderCallback(),
                            NfcAdapter.FLAG_READER_NFC_F,
                            null
                        )
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
            NfcTest(null, null)
        }
    }
}