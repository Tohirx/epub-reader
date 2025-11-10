package com.tohir.booksplusplus.data.database

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

    // --- Raw query methods ---
    @RawQuery
    suspend fun getDefinitionsRaw(query: SupportSQLiteQuery): List<DefinitionResult>

    @RawQuery
    suspend fun getPosRaw(query: SupportSQLiteQuery): PosResult?

    @RawQuery
    suspend fun getExamplesRaw(query: SupportSQLiteQuery): List<ExampleResult>

     // Returns all possible definitions for a given word.

    suspend fun getDefinitions(word: String): List<String> {
        val w = lemmatize(word)
        val query = SimpleSQLiteQuery(
            """
        SELECT DISTINCT s.definition
        FROM (
            SELECT wordid FROM words WHERE word = ?
            UNION
            SELECT wordid FROM casedwords WHERE casedword = ?
        ) AS allwords
        JOIN senses se ON allwords.wordid = se.wordid
        JOIN synsets s ON se.synsetid = s.synsetid
        LIMIT 10
        """,
            arrayOf(w, word)
        )

        return getDefinitionsRaw(query).mapNotNull { it.definition }
    }

    suspend fun getPos(word: String): String? {
        val w = lemmatize(word)
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
        LIMIT 1
        """,
            arrayOf(w, word)
        )

        return getPosRaw(query)?.pos
    }

    suspend fun getUsageExamples(word: String): List<String> {
        val w = lemmatize(word)
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



    // Very simple lemmatizer to normalize plural or inflected forms.
    fun lemmatize(word: String): String {
        var w = word.lowercase()

        when {
            w.endsWith("ies") -> w = w.dropLast(3) + "y" // bodies → body
            w.endsWith("es") && w.length > 3 -> w = w.dropLast(2) // boxes → box
            w.endsWith("s") && w.length > 2 -> w = w.dropLast(1) // books → book
        }

        when {
            w.endsWith("ing") && w.length > 4 -> w = w.dropLast(3) // running → run
            w.endsWith("ed") && w.length > 3 -> w = w.dropLast(2) // walked → walk
        }

        return w
    }
}
