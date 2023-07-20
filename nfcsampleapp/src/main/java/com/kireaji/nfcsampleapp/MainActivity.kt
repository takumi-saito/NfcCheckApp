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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val nfcScanViewModel = ViewModelProvider(this, NfcScanViewModelFactory(nfcAdapter)).get(NfcScanViewModelImpl::class.java)

        observeToViewModelEvent(nfcScanViewModel)

        setContent {
            NfcSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NfcScanScreen(nfcScanViewModel)
                }
            }
        }
    }

    private fun observeToViewModelEvent(viewModel: NfcScanViewModelImpl) {
        lifecycleScope.launch {
            viewModel.eventStartScan.collect {
                viewModel.enableNfcReaderMode(this@MainActivity)
            }
        }

        lifecycleScope.launch {
            viewModel.eventCancelScan.collect {
                viewModel.disableNfcReaderMode(this@MainActivity)
            }
        }
    }


    companion object {
        val TAG = MainActivity::class.java.simpleName
    }
}
