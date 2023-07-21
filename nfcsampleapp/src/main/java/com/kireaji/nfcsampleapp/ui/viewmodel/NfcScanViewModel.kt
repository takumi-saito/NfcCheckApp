package com.kireaji.nfcsampleapp.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kireaji.nfcsampleapp.MainActivity
import com.kireaji.nfcsampleapp.ui.screen.bytesToHexString
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface NfcScanViewModel {

    val stateNfc: StateFlow<NfcState>
    fun onClickStartScan()
    fun onClickCancelScan()
}

class NfcScanViewModelImpl(
    private val nfcAdapter: NfcAdapter
) : ViewModel(), NfcScanViewModel, NfcAdapter.ReaderCallback {

    private val _eventStartScan = MutableSharedFlow<Unit>()
    val eventStartScan: SharedFlow<Unit> = _eventStartScan

    private val _eventCancelScan = MutableSharedFlow<Unit>()
    val eventCancelScan: SharedFlow<Unit> = _eventCancelScan

    private val _eventStartPlayer = MutableSharedFlow<Unit>()
    val eventStartPlayer: SharedFlow<Unit> = _eventStartPlayer

    private val _eventStopPlayer = MutableSharedFlow<Unit>()
    val eventStopPlayer: SharedFlow<Unit> = _eventStopPlayer

    private val _stateNfc = MutableStateFlow<NfcState>(NfcState.None)
    override val stateNfc = _stateNfc.asStateFlow()

    override fun onClickStartScan() {
        viewModelScope.launch {
            _eventStartScan.emit(Unit)
        }
    }

    override fun onClickCancelScan() {
        viewModelScope.launch {
            _eventCancelScan.emit(Unit)
        }
    }

    fun enableNfcReaderMode(activity: Activity) {
        nfcAdapter.enableReaderMode(
            activity,
            this,
            NfcAdapter.FLAG_READER_NFC_F,
            null
        )
    }

    fun disableNfcReaderMode(activity: Activity) {
        nfcAdapter.disableReaderMode(activity)
    }
    override fun onTagDiscovered(tag: Tag) {
        Log.d(MainActivity.TAG, "Tag discoverd.")

        //get idm
        val idm = tag.id
        val idmString = bytesToHexString(idm)
        Log.d(MainActivity.TAG, idmString)

        if (idmString != "") {
            viewModelScope.launch {
                _eventStartPlayer.emit(Unit)
                _stateNfc.emit(NfcState.Success(idmString))
            }
            // scope.launch { state.hide() }
        }
    }
}

class NfcScanViewModelFactory(private val nfcAdapter: NfcAdapter) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NfcScanViewModelImpl(nfcAdapter) as T
    }
}

sealed interface NfcState {
    object None : NfcState
    object Ready : NfcState
    data class Success(
        val id: String
    ) : NfcState
}