package com.statuses.statussavers.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.ViewModel

class PageViewModel : ViewModel() {
    private val mIndex = MutableLiveData<Int>()
    val text: LiveData<String> =
        mIndex.map { input -> "Hello world from section: $input" }

    fun setIndex(index: Int) {
        mIndex.value = index
    }
}