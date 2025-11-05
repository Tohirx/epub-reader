package com.tohir.booksplusplus.data.database

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface DictionaryDao {

    data class DefinitionResult(
        val definition: String?
    )


    @RawQuery
    suspend fun getDefinitionRaw(query: androidx.sqlite.db.SupportSQLiteQuery): DefinitionResult?

    suspend fun getDefinition(word: String): String? {
        val query = SimpleSQLiteQuery(
            """
                SELECT synsets.definition
                FROM words
                JOIN senses ON words.wordid = senses.wordid
                JOIN synsets ON senses.synsetid = synsets.synsetid
                WHERE words.word = ?
                LIMIT 1
            """,
            arrayOf(word)
        )
        return getDefinitionRaw(query)?.definition
    }
}
