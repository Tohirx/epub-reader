package com.tohir.booksandstuff.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksandstuff.data.BooksRepository
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.util.BooksAndStuffApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val booksRepository: BooksRepository = BooksAndStuffApplication.booksRepository

    fun getRecentBooks(): Flow<List<Book>> {
        return booksRepository.getRecentBooks()

    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            booksRepository.updateBook(book)
        }
    }

}