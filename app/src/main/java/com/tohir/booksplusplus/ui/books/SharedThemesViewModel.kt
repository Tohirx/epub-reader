package com.tohir.booksplusplus.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedThemesViewModel : ViewModel() {
    private val _selectedTheme = MutableLiveData<Theme?>()
    val selectedTheme: LiveData<Theme?> = _selectedTheme

    private val _selectedFontFamily = MutableLiveData<String?>()
    val selectedFontFamily:  LiveData<String?> = _selectedFontFamily

    private val _selectedFontSize = MutableLiveData<Double>()
    val selectedFontSize:  LiveData<Double> = _selectedFontSize

    private val _selectedLineSpacing = MutableLiveData<Double>()
    val selectedLineSpacing: LiveData<Double> = _selectedLineSpacing


    fun setFontFamily(fontFamily: String) {
        this._selectedFontFamily.value = fontFamily
    }

    fun setFontSize(fontSize: Double) {
        this._selectedFontSize.value = fontSize
    }

    fun setLineSpacing(lineSpacing: Double) {
        this._selectedLineSpacing.value = lineSpacing
    }


    fun setTheme(theme: Theme) {
        _selectedTheme.value = theme
    }
}


class Theme {
     var backgroundColor: String
     var textColor: String
     var fontFamily: String

    constructor(backgroundColor: String, textColor: String, fontFamily: String) {
        this.backgroundColor = backgroundColor
        this.textColor = textColor
        this.fontFamily = fontFamily
    }



}