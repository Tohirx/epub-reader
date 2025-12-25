package com.tohir.booksplusplus.ui.books.reader.dictionary

import com.tohir.booksplusplus.data.database.dictionary.DictionaryModels

data class DictionaryResult(
    val selectedWord: String,
    val definitions: List<DictionaryModels.Definition>,
    val partOfSpeech: List<String> = emptyList(),
    val audioUrls: List<String> = emptyList()
)
