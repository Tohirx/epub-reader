package com.tohir.booksplusplus.ui.books.reader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.commit
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.ActivityReaderBinding
import com.tohir.booksplusplus.ui.books.LibraryFragment

class ReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toggleFullScreen()

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookIdFromLibraryFragmentOrHomeFragment =
            intent.getLongExtra(LibraryFragment.BOOK_ID, 0)


        val fragment = EpubReaderFragment().apply {
            arguments = bundleOf(
                "BOOK_ID" to bookIdFromLibraryFragmentOrHomeFragment
            )
        }

        supportFragmentManager.commit {
            replace(R.id.reader_container, fragment)
        }
    }


    private fun toggleFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}