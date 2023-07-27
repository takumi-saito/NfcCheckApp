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
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.kireaji.nfcsampleapp.ui.screen.NfcScanScreen
import com.kireaji.nfcsampleapp.ui.theme.NfcSampleTheme
import com.kireaji.nfcsampleapp.ui.viewmodel.NfcScanViewModelFactory
import com.kireaji.nfcsampleapp.ui.viewmodel.NfcScanViewModelImpl
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri

@UnstableApi
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
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }

    override fun onPause() {
        super.onPause()
        if(Util.SDK_INT <= 23){
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if(Util.SDK_INT > 23){
            releasePlayer()
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
        lifecycleScope.launch {
            viewModel.eventStartPlayer.collect {
                initializePlayer()
            }
        }
        lifecycleScope.launch {
            viewModel.eventStopPlayer.collect {
                releasePlayer()
            }
        }
    }

    private var player: ExoPlayer? = null

    private var playWhenReady = true //再生・一時停止の状態を保存
    private var currentItem = 0 //メディアアイテムのインデックスを保存
    private var playbackPosition = 0L //再生位置を保存


    private fun initializePlayer() {

        //メディアアイテム内のトラックを選択するtrackSelectorを作成、パラメータで選択条件を指定する
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        val player = player ?: ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setHandleAudioBecomingNoisy(true)
            .build().also {
                player = it
            }
        val mediaSource: MediaSource = ProgressiveMediaSource
            .Factory(DefaultDataSource.Factory(applicationContext))
            .createMediaSource(MediaItem.fromUri("https://firebasestorage.googleapis.com/v0/b/nfcsample-8db1c.appspot.com/o/sample.mp3?alt=media&token=80b1a41d-ace7-4a90-970e-42bfeb20d398"))

        // プレイヤーリセット
        player.stop(/* reset= */ true)
        player.playWhenReady = true
        player.prepare(mediaSource)

    }

    private fun releasePlayer() {
        //playerを破棄する前に情報を保存しておき、中断したところから再開できるようにする。
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.contentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }
}
