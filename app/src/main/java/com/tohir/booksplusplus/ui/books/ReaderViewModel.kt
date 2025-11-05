package com.tohir.booksplusplus.ui.books

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.util.BooksAndStuffApplication
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

class ReaderViewModel : ViewModel() {

    val publicationCache = object : LinkedHashMap<Int, Publication>(5, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int?, Publication?>?): Boolean {
            return size > 3
        }
    }

    private var publication: Publication? = null
    private val booksRepository = BooksAndStuffApplication.booksRepository
    suspend fun importPublication(uri: Uri, context: Context, bookId: Int?): Publication? {

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


    suspend fun saveReadingProgression(locator: Locator, bookID: Int) {
        val locatorString = locator.toJSON().toString()

        val progressValue = locator.locations.totalProgression

        booksRepository.saveReadingProgress(locatorString, bookID)

        if (progressValue != null)
            booksRepository.saveReadingProgressAsDouble(progressValue, bookID)

    }

    suspend fun restoreReadingProgression(bookID: Int): Locator? {


        val readingProgressLocator = booksRepository.getReadingProgress(bookID)

        if (readingProgressLocator != null) {
            val locatorJson = JSONObject(readingProgressLocator)
            val locator = Locator.fromJSON(locatorJson)

            return locator
        }

        return null

    }

}