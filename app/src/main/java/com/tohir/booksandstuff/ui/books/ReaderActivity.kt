package com.tohir.booksandstuff.ui.books

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.commit
import com.tohir.booksandstuff.R
import com.tohir.booksandstuff.databinding.ActivityReaderBinding

class ReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toggleFullScreen()

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookIdFromLibraryFragmentOrHomeFragment = intent.getLongExtra(LibraryFragment.BOOK_ID, 0)
        val bookUriFromLibraryFragmentOrHomeFragment = intent.getStringExtra(LibraryFragment.BOOK_URI)

        Log.d("tohir", "Reader Activity gotten $bookUriFromLibraryFragmentOrHomeFragment from Library Fragment")

        val uriFromFileManager: Uri? = intent?.data

        if (uriFromFileManager != null) {
            Log.d("tohir", "Reader Activity Gotten Uri: $uriFromFileManager from file manager")

            val fragment = EpubReaderFragment().apply {
                arguments = bundleOf(BOOK_PATH to uriFromFileManager.toString())
            }

            supportFragmentManager.commit {
                replace(R.id.reader_container, fragment)
            }
        } else {
            val fragment = EpubReaderFragment().apply {
                arguments = bundleOf(BOOK_URI to bookUriFromLibraryFragmentOrHomeFragment, "BOOK_ID" to bookIdFromLibraryFragmentOrHomeFragment)
            }

            supportFragmentManager.commit {
                replace(R.id.reader_container, fragment)
            }
        }

    }

    private fun toggleFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    companion object {
        const val BOOK_PATH = "BOOK_PATH"
        const val BOOK_URI = "BOOK_URI"
    }


}