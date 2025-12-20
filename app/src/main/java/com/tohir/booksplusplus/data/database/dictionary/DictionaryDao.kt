package com.tohir.booksplusplus.data.database.dictionary

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DictionaryDao {

    // --- Data classes for query results ---
    data class DefinitionResult(val definition: String?)
    data class PosResult(val pos: String?)
    data class ExampleResult(val sample: String?)

    @RawQuery
    suspend fun getDefinitionsRaw(query: SupportSQLiteQuery): List<DefinitionResult>

    @RawQuery
    suspend fun getPosRaw(query: SupportSQLiteQuery): List<PosResult>

    @RawQuery
    suspend fun getExamplesRaw(query: SupportSQLiteQuery): List<ExampleResult>

    suspend fun getDefinitions(word: String): List<String> {
        val w = word.lowercase()
        val query = SimpleSQLiteQuery(
            """
    SELECT DISTINCT s.definition AS definition
    FROM (
        SELECT wordid FROM words WHERE word = ?
        UNION
        SELECT wordid FROM casedwords WHERE casedword = ?
    ) AS allwords
    JOIN senses se ON allwords.wordid = se.wordid
    JOIN synsets s ON se.synsetid = s.synsetid
    """,
            arrayOf(w, word)
        )

        return getDefinitionsRaw(query).mapNotNull { it.definition }
    }

    suspend fun getPos(word: String): List<String> {
        val w = word.lowercase()
        val query = SimpleSQLiteQuery(
            """
        SELECT DISTINCT p.pos
        FROM (
            SELECT wordid FROM words WHERE word = ?
            UNION
            SELECT wordid FROM casedwords WHERE casedword = ?
        ) AS allwords
        JOIN senses se ON allwords.wordid = se.wordid
        JOIN synsets s ON se.synsetid = s.synsetid
        JOIN poses p ON s.posid = p.posid
        """,
            arrayOf(w, word)
        )

        return getPosRaw(query).mapNotNull { it.pos }
    }

    suspend fun getUsageExamples(word: String): List<String> {
        val w = word.lowercase()
        val query = SimpleSQLiteQuery(
            """
            SELECT DISTINCT sm.sample
            FROM (
                SELECT wordid FROM words WHERE word = ?
                UNION
                SELECT wordid FROM casedwords WHERE casedword = ?
            ) AS allwords
            JOIN senses se ON allwords.wordid = se.wordid
            JOIN samples sm ON se.synsetid = sm.synsetid
            LIMIT 5
        """,
            arrayOf(w, word)
        )

        return getExamplesRaw(query).mapNotNull { it.sample }

    }
}