package com.tohir.booksplusplus.ui.books.reader.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.model.Note
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Locator

class NoteViewModel : ViewModel() {
    private val booksRepository = BooksPlusPlus.booksRepository

     fun addNote(locator: Locator, bookId: Long, content: String) {
        viewModelScope.launch {
            val highlight = booksRepository.findLastHighlightAdded()
            booksRepository.addNote(Note(locator, content, bookId, highlight.id ))
        }
    }

    suspend fun findNoteById(id: Long): Note {
        return booksRepository.findNoteById(id)
    }

    suspend fun updateNote(note: Note) {
        booksRepository.updateNote(note)
    }

     fun deleteNoteById(id: Long) {

         viewModelScope.launch {
             booksRepository.deleteNoteById(id)
         }

    }


}