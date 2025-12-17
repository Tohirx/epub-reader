package com.tohir.booksplusplus.ui.books

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

class LibraryFragmentViewModel : ViewModel() {


    private val booksRepository: BooksRepository = BooksPlusPlus.booksRepository

    fun deleteBook(book: Book) {

        book.cover?.let { File(book.cover).delete() }
        book.uri.let { File(book.uri.toUri().path!!).delete() }

        viewModelScope.launch {
            booksRepository.deleteBook(book)
        }
    }

    fun getAllBooks(): Flow<List<Book>> {
        return booksRepository.getAllBooks()
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            booksRepository.updateBook(book)
        }
    }


}