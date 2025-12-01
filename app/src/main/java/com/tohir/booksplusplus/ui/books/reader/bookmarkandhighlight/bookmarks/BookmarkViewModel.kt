package com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.bookmarks

import androidx.lifecycle.ViewModel
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.flow.Flow

class BookmarkViewModel : ViewModel() {
    private val booksRepository = BooksPlusPlus.booksRepository

      fun getAllBookmarks(bookId: Long): Flow<List<Bookmark>> {

        return booksRepository.getAllBookmarks(bookId)
    }

     suspend fun deleteBookMark(id: Long) {
        booksRepository.deleteBookmark(id)
    }
}