package com.tohir.booksplusplus.ui.books.reader.dictionary

import com.google.gson.Gson
import com.tohir.booksplusplus.ui.books.reader.dictionary.DictionaryModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class DictionaryApi {
    private val gson = Gson()
    private val baseUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/"

    suspend fun lookup(word: String): Result<List<DictionaryModels.WordEntry>> =
        withContext(Dispatchers.IO) {
            try {
                val response = URL(baseUrl + word).readText()
                val entries =
                    gson.fromJson(response, Array<DictionaryModels.WordEntry>::class.java).toList()
                Result.success(entries)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
}