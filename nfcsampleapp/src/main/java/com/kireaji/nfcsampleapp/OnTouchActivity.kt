package com.kireaji.nfcsampleapp

import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kireaji.nfcsampleapp.ui.screen.NfcScanScreen
import com.kireaji.nfcsampleapp.ui.theme.NfcSampleTheme
import com.kireaji.nfcsampleapp.ui.viewmodel.NfcScanViewModelFactory
import com.kireaji.nfcsampleapp.ui.viewmodel.NfcScanViewModelImpl
import kotlinx.coroutines.launch

class OnTouchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            NfcSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                }
            }
        }
    }



    companion object {
        val TAG = OnTouchActivity::class.java.simpleName
    }
}
