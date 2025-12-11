package com.tohir.booksplusplus.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val booksRepository: BooksRepository = BooksPlusPlus.booksRepository

     fun getRecentBooks(): Flow<List<Book>> {
        return booksRepository.getRecentBooks()
    }

    fun getFinishedBooks(): Flow<List<Book>> {
        return booksRepository.getFinishedBooks()
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            booksRepository.updateBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            booksRepository.deleteBook(book)
        }
    }

}