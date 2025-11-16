package com.tohir.booksplusplus.ui.books

import androidx.lifecycle.ViewModel
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow

class HighlightFragmentViewModel : ViewModel() {
    private val booksRepository: BooksRepository = BooksPlusPlus.booksRepository


    fun getAllHighlights(bookId: Long): Flow<List<Highlight>> {
       return booksRepository.getAllHighlights(bookId)
    }
}