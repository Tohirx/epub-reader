package com.tohir.booksplusplus.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.util.BooksPlusPlusApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LibraryFragmentViewModel : ViewModel() {


    private val booksRepository: BooksRepository = BooksPlusPlusApplication.booksRepository

    fun deleteBook(book: Book) {
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