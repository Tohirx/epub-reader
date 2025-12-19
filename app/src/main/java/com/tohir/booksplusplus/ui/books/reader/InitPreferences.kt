package com.tohir.booksplusplus.ui.books.reader

import android.content.Context
import androidx.core.graphics.toColorInt
import com.tohir.booksplusplus.ui.books.reader.EpubReaderFragment.Companion.TEXT_COLOR
import org.readium.r2.navigator.preferences.Color
import org.readium.r2.navigator.preferences.ColumnCount
import org.readium.r2.navigator.preferences.FontFamily
import org.readium.r2.navigator.preferences.TextAlign

class InitPreferences(
    var context: Context,
    var fontFamily: FontFamily = FontFamily.SERIF,
    var fontSize: Double = 1.0,
    var lineSpacing: Double = 1.2,
    var publisherStyles: Boolean = false,
    var columnCount: ColumnCount = ColumnCount.ONE,
    var textAlign: TextAlign = TextAlign.START,
    var scroll: Boolean = false,
    var backgroundColor: Color = Color("#FFFFFF".toColorInt()),
    var textColor: Color = Color("#000000".toColorInt())
) {

    init {
        val pref = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        fontFamily = FontFamily(pref.getString(EpubReaderFragment.FONT_FAMILY, "Lexend") ?: "Lexend")
        fontSize = pref.getFloat(EpubReaderFragment.FONT_SIZE, 1.0f).toDouble()
        lineSpacing = pref.getFloat(EpubReaderFragment.LINE_HEIGHT, 1.2f).toDouble()
        backgroundColor = Color(pref.getString(EpubReaderFragment.BACKGROUND_COLOR, "#FFFFFF")!!.toColorInt())
        textColor = Color(pref.getString(TEXT_COLOR, "#000000")!!.toColorInt())
    }


}
