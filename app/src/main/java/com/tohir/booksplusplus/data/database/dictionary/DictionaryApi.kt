package com.tohir.booksplusplus.data.database.dictionary

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.jvm.java

class DictionaryApi {

    // Moshi instance with Kotlin support
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // handles default values and nullables
        .build()

    private val baseUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/"

    suspend fun lookup(word: String): Result<List<DictionaryModels.WordEntry>> =
        withContext(Dispatchers.IO) {
            try {
                // Fetch JSON from API
                val response = URL(baseUrl + word).readText()

                // Prepare adapter for List<WordEntry>
                val type = Types.newParameterizedType(
                    List::class.java,
                    DictionaryModels.WordEntry::class.java
                )
                val adapter = moshi.adapter<List<DictionaryModels.WordEntry>>(type)

                // Deserialize JSON
                val entries = adapter.fromJson(response) ?: emptyList()
                Result.success(entries)

            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
}