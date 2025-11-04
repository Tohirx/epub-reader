package com.tohir.booksandstuff.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationBarView
import com.tohir.booksandstuff.ui.books.HomeFragment
import com.tohir.booksandstuff.ui.books.LibraryFragment
import com.tohir.booksandstuff.R
import com.tohir.booksandstuff.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNav.setOnItemSelectedListener(this)

        setContentView(binding.root)

    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_home -> {
                onHomeButtonClick()
                return true
            }

            else -> {
                onLibraryButtonClick()
                return true
            }
        }
    }

    private fun onLibraryButtonClick() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, LibraryFragment())
        }
    }
    private fun onHomeButtonClick() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, HomeFragment())
        }
    }

}