package com.tohir.booksplusplus.ui.books.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

class ReaderViewModel : ViewModel() {

    private val _selectedTheme = MutableLiveData<Theme>()
    val selectedTheme: LiveData<Theme> = _selectedTheme
    private val _bookmark = MutableLiveData<Bookmark>()
    val bookmark: LiveData<Bookmark> = _bookmark
    private val _page = MutableLiveData<Int>()
    val page: LiveData<Int> = _page

    private val _link = MutableLiveData<Link>()
    val link: LiveData<Link> = _link

    private val _publication = MutableLiveData<Publication>()
    val publication: LiveData<Publication> = _publication

    private val _selectedFontFamily = MutableLiveData<String>()
    val selectedFontFamily: LiveData<String> = _selectedFontFamily

    private val _selectedFontSize = MutableLiveData<Double>()
    val selectedFontSize: LiveData<Double> = _selectedFontSize

    private val _selectedLineSpacing = MutableLiveData<Double>()
    val selectedLineSpacing: LiveData<Double> = _selectedLineSpacing

    private val _locator = MutableLiveData<Locator>()
    val locator: LiveData<Locator> = _locator

    private val _selectedHighlight = MutableLiveData<Highlight?>()
    var selectedHighlight: LiveData<Highlight?> = _selectedHighlight

    private val _letterSpacing = MutableLiveData<Double>()
    var selectedLetterSpacing: LiveData<Double> = _letterSpacing

    private val _wordSpacing = MutableLiveData<Double>()
    var selectedWordSpacing: LiveData<Double> = _wordSpacing

    fun setFontFamily(fontFamily: String) {
        this._selectedFontFamily.value = fontFamily
    }

    fun setBookmark(bookmark: Bookmark) {
        _bookmark.value = bookmark
    }

    fun setLink(link: Link) {
        _link.value = link
    }

    fun setLocator(locator: Locator) {
        _locator.value = locator
    }

    fun setFontSize(fontSize: Double) {
        _selectedFontSize.value = fontSize
    }

    fun setLineSpacing(lineSpacing: Double) {
        _selectedLineSpacing.value = lineSpacing
    }

    fun setLetterSpacing(letterSpacing: Double) {
        _letterSpacing.value = letterSpacing
    }

    fun setWordSpacing(wordSpacing: Double) {
        _wordSpacing.value = wordSpacing
    }


    fun setTheme(theme: Theme) {
        _selectedTheme.value = theme
    }

    fun setPublication(publication: Publication) {
        _publication.value = publication
    }

    fun setPage(page: Int) {
        _page.value = page
    }


    fun setHighlight(highlight: Highlight?) {
        _selectedHighlight.value = highlight
    }
}


data class Theme(val backgroundColor: String, val textColor: String, val fontFamily: String)