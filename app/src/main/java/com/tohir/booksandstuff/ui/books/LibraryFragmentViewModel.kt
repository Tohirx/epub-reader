package com.tohir.booksandstuff.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksandstuff.data.BooksRepository
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.util.BooksAndStuffApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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

    fun updateBook(book: Book) {
        viewModelScope.launch {
            booksRepository.updateBook(book)
        }
    }


}