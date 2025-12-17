package com.tohir.booksplusplus.ui.books.reader

import android.content.Context
import android.net.Uri
import android.support.annotation.ColorInt
import androidx.lifecycle.ViewModel
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.data.model.Note
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class EpubReaderViewModel : ViewModel() {

    val publicationCache = object : LinkedHashMap<Long, Publication>(5, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Long?, Publication?>?): Boolean {
            return size > 3
        }
    }

    private var publication: Publication? = null
    private val booksRepository = BooksPlusPlus.booksRepository
    suspend fun importPublication(context: Context, bookId: Long): Publication? {

        if (!publicationCache.contains(bookId)) {

            val book = booksRepository.findBookById(bookId)
            val httpClient = DefaultHttpClient()
            val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
            val url: AbsoluteUrl? = Uri.parse(book.uri).toAbsoluteUrl()

            val asset = assetRetriever.retrieve(url!!).getOrNull()

            if (asset != null) {
                val publicationParser = DefaultPublicationParser(
                    context,
                    httpClient,
                    assetRetriever,
                    PdfiumDocumentFactory(context)
                )

                val publicationOpener = PublicationOpener(publicationParser)

                publication =
                    publicationOpener.open(asset, allowUserInteraction = false).getOrNull()


                publicationCache.putIfAbsent(bookId, publication!!)

                return publication

            }


        } else {
            return publicationCache[bookId]
        }

        return null
    }


    suspend fun saveReadingProgression(locator: Locator, bookID: Long) {
        val locatorString = locator.toJSON().toString()

        val progress = locator.locations.totalProgression

        booksRepository.saveReadingProgress(locatorString, bookID)

        if (progress != null) booksRepository.saveReadingProgressAsDouble(progress, bookID)

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