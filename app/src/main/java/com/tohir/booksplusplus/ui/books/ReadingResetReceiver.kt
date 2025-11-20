package com.tohir.booksplusplus.ui.books

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.edit

class ReadingResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        prefs.edit { putInt("MINUTES", 0) }
    }


}