package com.tohir.booksplusplus.ui

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationBarView
import com.tohir.booksplusplus.ui.books.HomeFragment
import com.tohir.booksplusplus.ui.books.LibraryFragment
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNav.setOnItemSelectedListener(this)

        setContentView(binding.root)

        val uriFromFileManager: Uri? = intent?.data

        if (uriFromFileManager != null)
            lifecycleScope.launch { viewModel.addBookPublicationToDatabase(uriFromFileManager, this@MainActivity) }

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