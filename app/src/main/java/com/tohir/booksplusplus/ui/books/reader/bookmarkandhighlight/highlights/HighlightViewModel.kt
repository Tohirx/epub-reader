package com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.highlights

import androidx.lifecycle.ViewModel
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.data.model.Note
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow

class HighlightViewModel : ViewModel() {
    private val booksRepository: BooksRepository = BooksPlusPlus.booksRepository


    fun getAllHighlights(bookId: Long): Flow<List<Highlight>> {
       return booksRepository.getAllHighlights(bookId)
    }

    fun getAllNotes(bookId: Long): Flow<List<Note>> {
        return booksRepository.getAllNotes(bookId)
    }

    suspend fun deleteHighlight(id: Long) {
        booksRepository.deleteHighlightById(id)
    }
}