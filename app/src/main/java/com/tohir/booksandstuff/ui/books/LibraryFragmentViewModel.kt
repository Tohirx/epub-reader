package com.tohir.booksandstuff.ui.books

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksandstuff.data.BooksRepository
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.util.BooksAndStuffApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Try
import org.readium.r2.shared.util.asset.Asset
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser

class LibraryFragmentViewModel : ViewModel() {

    private lateinit var publication: Publication

    private val booksRepository: BooksRepository = BooksAndStuffApplication.booksRepository

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            booksRepository.deleteBook(book)
        }
    }

    fun importPublication(uri: Uri, context: Context, onReady: (Publication) -> Unit) {
        viewModelScope.launch {

            val httpClient = DefaultHttpClient()
            val assetRetriever = AssetRetriever(context.contentResolver, httpClient)

            val url: AbsoluteUrl? = uri.toAbsoluteUrl()

            val assetResult = assetRetriever.retrieve(url!!).getOrNull()

            val asset = assetResult


            val publicationParser = DefaultPublicationParser(
                context, httpClient, assetRetriever,
                PdfiumDocumentFactory(context)
            )

            val publicationOpener = PublicationOpener(publicationParser)

            publication =
                publicationOpener.open(asset!!, allowUserInteraction = false).getOrNull()!!

            BooksAndStuffApplication.currentPublication = publication


            onReady(publication)

        }


    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            booksRepository.addBook(book)
        }
    }

    fun getAllBooks(): Flow<List<Book>> {
        return booksRepository.getAllBooks()
    }



}