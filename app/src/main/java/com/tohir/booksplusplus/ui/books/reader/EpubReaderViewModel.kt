package com.tohir.booksplusplus.ui.books.reader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.ColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
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
    suspend fun importPublication(uri: Uri, context: Context, bookId: Long?): Publication? {

        if (bookId != null && !publicationCache.contains(bookId)) {

            val fileFromStorage = copyUriToInternalStorage(uri, context)

            if (fileFromStorage != null) {

                val httpClient = DefaultHttpClient()
                val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
                val url: AbsoluteUrl? = Uri.fromFile(fileFromStorage).toAbsoluteUrl()

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

                }

                if (publication != null) {

                    val authors = publication!!.metadata.authors.joinToString(", ") { contributor ->
                        contributor.name
                    }

                    // PLEASE FIX THIS LATER. GET A SUITABLE PLACEHOLDER IMAGE FOR BOOKS MISSING A COVER, AND STORE IT THE FILE.
                    // This stores the cover in the App's directory as a PNG image


                    val file = File(context.filesDir, "cover_${publication!!.metadata.title}.png")
                    if (publication!!.cover() != null && !file.exists()) {
                        FileOutputStream(file).use { out ->
                            publication!!.cover()?.compress(Bitmap.CompressFormat.PNG, 80, out)
                        }
                    }


                    val mediaType = asset?.format?.mediaType
                    viewModelScope.launch {

                        val books = booksRepository.getAllBooksAsList()
                        val book = Book(
                            title = publication!!.metadata.title,
                            author = authors,
                            cover = file.absolutePath,
                            identifier = publication!!.metadata.identifier ?: "",
                            uri = Uri.fromFile(fileFromStorage).toString(),
                            readingProgressJSON = null,
                            mediaType = mediaType.toString(),
                            lastDateOpened = LocalDateTime.now().toString(),
                            readingProgressDouble = null
                        )
                        if (!books.contains(book))
                            booksRepository.addBook(book)

                    }

                    publicationCache.putIfAbsent(bookId, publication!!)

                    return publication

                }

            }

        } else {
            return publicationCache[bookId]
        }

        return null
    }

    companion object {
        fun copyUriToInternalStorage(sourceUri: Uri, context: Context): File? {

            val existingFile = File(context.filesDir, sourceUri.lastPathSegment ?: "book.epub")

            if (existingFile.exists()) return existingFile

            try {
                val fileName = "file_${System.currentTimeMillis()}.epub"
                val destinationFile = File(context.filesDir, fileName)

                context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                return destinationFile

            } catch (e: Exception) {
                println(e.message)
                println(e.printStackTrace())
            }

            return null
        }
    }

    suspend fun saveReadingProgression(locator: Locator, bookID: Long) {
        val locatorString = locator.toJSON().toString()

        val progressValue = locator.locations.totalProgression

        booksRepository.saveReadingProgress(locatorString, bookID)

        if (progressValue != null)
            booksRepository.saveReadingProgressAsDouble(progressValue, bookID)

    }


    suspend fun addBookmark(bookId: Long, locator: Locator) {

        val bookmark = Bookmark(bookId, locator, getCurrentFormattedDate())
        booksRepository.addBookmark(bookmark)

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