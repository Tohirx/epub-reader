package com.tohir.booksplusplus.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationBarView
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.ActivityMainBinding
import com.tohir.booksplusplus.ui.books.HomeFragment
import com.tohir.booksplusplus.ui.books.LibraryFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private val prefs: SharedPreferences by lazy {
        this.getSharedPreferences("user_pref", MODE_PRIVATE)
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNav.setOnItemSelectedListener(this)

        seedDatabase()
        handleIncomingUri(intent)


    }

    private fun seedDatabase() {
        val firstRun = prefs.getBoolean("first_run", true)
        if (firstRun) {
            lifecycleScope.launch {
                viewModel.addBookFromAsset(
                    this@MainActivity,
                    "jane-austen_pride-and-prejudice.epub"
                )
                viewModel.addBookFromAsset(this@MainActivity, "mary-shelley_frankenstein.epub")
                prefs.edit {
                    putBoolean("first_run", false)
                }
            }

        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingUri(intent)
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

    private fun handleIncomingUri(intent: Intent?) {
        val uriFromFileManager: Uri? = intent?.data

        if (uriFromFileManager != null)
            viewModel.addBookPublicationToDatabase(uriFromFileManager, this@MainActivity)
    }


}