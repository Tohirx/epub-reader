package com.tohir.booksplusplus.dictionary

class DictionaryModels {

    data class WordEntry(
        val word: String,
        val phonetics: List<Phonetic> = emptyList(),
        val meanings: List<Meaning> = emptyList()
    )

    data class Phonetic(
        val text: String? = null,
        val audio: String? = null
    )

    data class Meaning(
        val partOfSpeech: String,
        val definitions: List<Definition>
    )

    data class Definition(
        val definition: String,
        val example: String? = null,
        val synonyms: List<String>? = null,
        val antonyms: List<String>? = null
    )

}