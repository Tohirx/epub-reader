package com.tohir.booksandstuff.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.readium.r2.shared.util.mediatype.MediaType

@Entity(indices = [Index(value = ["identifier"], unique = true)])
data class Book(

    @PrimaryKey(autoGenerate = true) val bookId: Int = 0,
    val title: String?,
    val lastDateOpened: String?,
    val dateAdded: Long = 0,
    val author: String?,
    val cover: String?,
    val uri: String,
    val identifier: String?,
    val readingProgress: String?,
    val mediaType: String?
)