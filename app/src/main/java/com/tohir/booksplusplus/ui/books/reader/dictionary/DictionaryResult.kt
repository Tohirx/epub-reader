package com.tohir.booksplusplus.ui.books.reader.dictionary

data class DictionaryResult(
    val selectedWord: String,
    val definitions: List<String>,
    val partOfSpeech: List<String> = emptyList(),
    val usages: List<String> = emptyList(),
    val audioUrls: List<String> = emptyList()
)
