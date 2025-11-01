package com.tohir.booksandstuff.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["identifier"], unique = true)])
data class Book(

     @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val title: String?,
    val lastDateOpened: String?,
    val dateAdded: Long = 0,
    val author: String?,
    val cover: String?,
    val uri: String,
    val identifier: String?,
    val readingProgressJSON: String?,
    val readingProgressDouble: Double?,
    val mediaType: String?,
    val isComplete: Boolean = false,
)

