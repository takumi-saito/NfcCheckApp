package com.example.nfcsample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class MainViewModel : ViewModel() {
    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url
    fun setUrl(url: String) {
        _url.value = url
    }
}