package com.tohir.booksplusplus.ui.books.reader.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.model.Note
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Locator
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NoteViewModel : ViewModel() {
    private val booksRepository = BooksPlusPlus.booksRepository

     fun addNote(locator: Locator, bookId: Long, content: String) {
        viewModelScope.launch {
            booksRepository.addNote(Note(locator, content, bookId, getCurrentFormattedDate()))
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

    fun getCurrentFormattedDate(): String {
        val now = ZonedDateTime.now()
        val format = "EEEE, d MMMM, YYYY"
        val formatter = DateTimeFormatter.ofPattern(format).withLocale(Locale.ENGLISH)
        return now.format(formatter)
    }



}