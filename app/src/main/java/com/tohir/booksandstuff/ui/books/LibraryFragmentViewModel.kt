package com.tohir.booksandstuff.ui.books

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksandstuff.data.BooksRepository
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.util.BooksAndStuffApplication
import com.tohir.booksandstuff.util.BooksAndStuffApplication.Companion.booksRepository
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


    private val booksRepository: BooksRepository = BooksAndStuffApplication.booksRepository

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            booksRepository.deleteBook(book)
        }
    }

    fun getAllBooks(): Flow<List<Book>> {
        return booksRepository.getAllBooks()
    }



}