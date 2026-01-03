package com.tohir.booksplusplus.ui.books

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.appbar.MaterialToolbar
import com.tohir.booksplusplus.databinding.ActivityAboutBinding

class ActivityAbout : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()

        val toolbar: MaterialToolbar = binding.aboutToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

    }

    fun setupClickListeners() {
        binding.buttonLicenses.setOnClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        }

        binding.buttonPrivacy.setOnClickListener {
            startActivity(Intent(this, ActivityPrivacyPolicy::class.java))
        }

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> finish()
        }

        return true
    }
}