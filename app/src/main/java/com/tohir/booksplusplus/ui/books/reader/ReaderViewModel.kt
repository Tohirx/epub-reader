package com.tohir.booksplusplus.ui.books.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tohir.booksplusplus.data.model.Highlight
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

class ReaderViewModel : ViewModel() {

    private val _selectedTheme = MutableLiveData<Theme>()
    val selectedTheme: LiveData<Theme> = _selectedTheme
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

    private val _selectedHighlight = MutableLiveData<Highlight?>()

    private val _locator = MutableLiveData<Locator>()
    val locator: LiveData<Locator> = _locator
    var selectedHighlight: LiveData<Highlight?> = _selectedHighlight

    fun setFontFamily(fontFamily: String) {
        this._selectedFontFamily.value = fontFamily
    }

    fun setLink(link: Link) {
        _link.value = link
    }

    fun setLocator(locator: Locator) {
        _locator.value = locator
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


data class Theme(val backgroundColor: String, val textColor: String, val fontFamily: String) {


}