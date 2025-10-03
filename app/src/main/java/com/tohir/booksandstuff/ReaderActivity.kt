package com.tohir.booksandstuff

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.tohir.booksandstuff.databinding.ActivityReaderBinding
import com.tohir.booksandstuff.ui.books.EpubReaderFragment
import com.tohir.booksandstuff.util.BooksAndStuffApplication

class ReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReaderBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReaderBinding.inflate(layoutInflater)


        setContentView(binding.root)



    }

}