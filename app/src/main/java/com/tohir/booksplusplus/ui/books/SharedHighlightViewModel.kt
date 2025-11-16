package com.tohir.booksplusplus.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tohir.booksplusplus.data.model.Highlight

class SharedHighlightViewModel : ViewModel() {

    private val _selectedHighlight = MutableLiveData<Highlight?>()
    val selectedHighlight: LiveData<Highlight?> = _selectedHighlight

    fun setHighlight(highlight: Highlight) {
        _selectedHighlight.value = highlight
    }

}