package com.tohir.booksplusplus.data.database.dictionary

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object DictionaryApi {

    private val cache = object : LinkedHashMap<String, List<DictionaryModels.WordEntry>>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<DictionaryModels.WordEntry>>): Boolean {
            return size > 10
        }
    }

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val type = Types.newParameterizedType(
        List::class.java,
        DictionaryModels.WordEntry::class.java
    )

    private val adapter = moshi.adapter<List<DictionaryModels.WordEntry>>(type)

    private val baseUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/"

    suspend fun lookup(word: String): Result<List<DictionaryModels.WordEntry>> {
        cache[word]?.let {
            return Result.success(it)
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = URL(baseUrl + word).readText()
                val entries = adapter.fromJson(response) ?: emptyList()

                cache[word] = entries
                Result.success(entries)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}