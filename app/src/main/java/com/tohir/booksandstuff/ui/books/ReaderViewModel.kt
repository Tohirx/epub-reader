package com.tohir.booksandstuff.ui.books

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.util.BooksAndStuffApplication
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

    private lateinit var publication: Publication

    private val booksRepository = BooksAndStuffApplication.booksRepository
    private val publications = mutableMapOf<Int, Publication>()

    suspend fun importPublication(uri: Uri, context: Context, bookID: Int?): Publication {


        if (!publications.contains(bookID)) {
            val destFile = persistEpubFile(context, uri)

            val httpClient = DefaultHttpClient()
            val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
            val fileUri = Uri.fromFile(destFile)
            val url: AbsoluteUrl? = fileUri.toAbsoluteUrl()

            val asset =
                assetRetriever.retrieve(url!!)
                    .getOrNull()


            val publicationParser = DefaultPublicationParser(
                context,
                httpClient,
                assetRetriever,
                PdfiumDocumentFactory(context)
            )


            val publicationOpener = PublicationOpener(publicationParser)

            publication =
                publicationOpener.open(asset!!, allowUserInteraction = false).getOrNull()!!

            val authors = publication.metadata.authors.joinToString(", ") { contributor ->
                contributor.name
            }

            //This stores the cover in the App's directory as a PNG image
            val file =
                File(context.filesDir, "cover_${publication.metadata.title}.png")
            FileOutputStream(file).use { out ->
                publication.cover()!!.compress(Bitmap.CompressFormat.PNG, 80, out)
            }


            val mediaType = asset.format.mediaType
            viewModelScope.launch {

                val books = booksRepository.getAllBooksAsList()
                val book = Book(
                    title = publication.metadata.title,
                    author = authors,
                    cover = file.absolutePath,
                    identifier = publication.metadata.identifier ?: "",
                    uri = destFile.absolutePath,
                    readingProgress = null,
                    mediaType = mediaType.toString(),
                    lastDateOpened = LocalDateTime.now().toString()
                )
                if (!books.contains(book)) {

                    booksRepository.addBook(book)
                }

                publications.putIfAbsent(bookID!!, publication)

            }


            return publication

        }

        return publications[bookID]!!

    }

    fun persistEpubFile(context: Context, sourceUri: Uri): File {
        val fileName = "book_${System.currentTimeMillis()}.epub"
        val destFile = File(context.filesDir, fileName)
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return destFile
    }

    suspend fun saveReadingProgression(locator: Locator, bookID: Int) {
        val locatorString = locator.toJSON().toString()

        booksRepository.saveReadingProgress(locatorString, bookID)

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