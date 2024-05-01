package com.zimoliv.buttonrush.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val number = MutableLiveData<Int>().apply { value = -1 }
    val textProgress: LiveData<Int> = number
}