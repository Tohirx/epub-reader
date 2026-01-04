package com.tohir.booksplusplus.data.database.dictionary

import com.squareup.moshi.JsonClass

object DictionaryModels {

    @JsonClass(generateAdapter = true)
    data class WordEntry(
        val word: String,
        val phonetics: List<Phonetic> = emptyList(),
        val meanings: List<Meaning> = emptyList()
    )

    @JsonClass(generateAdapter = true)
    data class Phonetic(
        val text: String? = null,
        val audio: String? = null
    )

    @JsonClass(generateAdapter = true)
    data class Meaning(
        val partOfSpeech: String,
        val definitions: List<Definition>,
        val synonyms: List<String>,
        val antonyms: List<String>

    )

    @JsonClass(generateAdapter = true)
    data class Definition(
        val definition: String,
        val example: String? = null,
        val synonyms: List<String>? = null,
        val antonyms: List<String>? = null
    )
}
