package com.tohir.booksplusplus.ui.books.reader

import android.content.Context
import android.support.annotation.ColorInt
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.data.model.Note
import com.tohir.booksplusplus.ui.books.publicationparser.PublicationParser
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.Asset
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class EpubReaderViewModel : ViewModel() {

    data class ReaderState(
        val publication: Publication? = null,
        val locator: Locator? = null,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    val publicationCache = object : LinkedHashMap<Long, Publication>(5, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Long?, Publication?>?): Boolean {
            return size > 3
        }
    }
    private val booksRepository = BooksPlusPlus.booksRepository
    private val _readerState = MutableStateFlow(ReaderState())
    val readerState: StateFlow<ReaderState> = _readerState.asStateFlow()

    fun loadBook(context: Context, bookId: Long) {
        viewModelScope.launch {
            val publication = getPublication(context, bookId)
            if (publication == null) {
                _readerState.update { it.copy(isLoading = false, error = "Failed to parse publication") }
                return@launch
            }
            val locator = restoreReadingProgression(bookId)
            _readerState.update {
                it.copy(publication = publication, locator = locator, isLoading = false)
            }
        }
    }
    suspend fun getPublication(context: Context, bookId: Long): Publication? {

        if (publicationCache.contains(bookId))
            return publicationCache[bookId]

        val publication = PublicationParser.retrievePublication(context, bookId)
        if (publication != null) publicationCache[bookId] = publication

        return publication
    }


    suspend fun saveReadingProgression(locator: Locator, bookID: Long) {
        val locatorString = locator.toJSON().toString()
        val progress = locator.locations.totalProgression
        booksRepository.saveReadingProgress(locatorString, bookID)
        if (progress != null)
            booksRepository.saveReadingProgressAsDouble(progress, bookID)
    }


    suspend fun addBookmark(bookId: Long, locator: Locator) {

        val bookmark = Bookmark(bookId, locator, getCurrentFormattedDate())
        booksRepository.addBookmark(bookmark)

    }

    fun getAllNotes(bookID: Long): Flow<List<Note>> {
        return booksRepository.getAllNotes(bookID)
    }

    fun getAllBookmarks(bookID: Long): Flow<List<Bookmark>> {
        return booksRepository.getAllBookmarks(bookID)
    }

    fun getCurrentFormattedDate(): String {
        val now = ZonedDateTime.now()
        val format = "EEEE, d MMMM, YYYY"
        val formatter = DateTimeFormatter.ofPattern(format).withLocale(Locale.ENGLISH)
        return now.format(formatter)
    }

    suspend fun addHighlight(
        bookID: Long,
        style: Highlight.Style,
        @ColorInt tint: Int,
        locator: Locator,
        annotation: String = "",
    ) {
        booksRepository.addHighlight(
            bookID,
            style,
            tint,
            locator,
            annotation,
            getCurrentFormattedDate()
        )
    }

    fun getAllHighlights(bookID: Long): Flow<List<Highlight>> {
        return booksRepository.getAllHighlights(bookID)
    }

    suspend fun deleteHighlightById(id: Long) {
        booksRepository.deleteHighlightById(id)
    }

    suspend fun updateHighlight(id: Long, tint: Int) {
        val highlight: Highlight = booksRepository.findHighlightById(id)
        val highlightCopy = highlight.copy(tint = tint)

        booksRepository.updateHighlight(highlightCopy)

    }

    suspend fun restoreReadingProgression(bookID: Long): Locator? {

        val readingProgressLocator = booksRepository.getReadingProgress(bookID)

        if (readingProgressLocator != null) {
            val locatorJson = JSONObject(readingProgressLocator)
            val locator = Locator.fromJSON(locatorJson)

            return locator
        }

        return null

    }
}