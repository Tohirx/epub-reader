package com.tohir.booksplusplus.ui.books.reader.dictionary

data class DictionaryResult(
    val selectedWord: String,
    val definitions: List<String>,
    val partOfSpeech: String?,
    val usages: List<String>,
    val audioUrl: String? = null
)
