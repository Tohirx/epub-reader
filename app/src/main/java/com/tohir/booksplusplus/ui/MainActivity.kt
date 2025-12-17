package com.tohir.booksplusplus.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationBarView
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.ActivityMainBinding
import com.tohir.booksplusplus.ui.books.HomeFragment
import com.tohir.booksplusplus.ui.books.LibraryFragment
import com.tohir.booksplusplus.ui.books.ReadingResetReceiver
import java.util.Calendar

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.bottomNav.setOnItemSelectedListener(this)

        scheduleDailyReset(this)

        handleIncomingUri(intent)
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

    fun scheduleDailyReset(context: Context) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If it's already past midnight today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, ReadingResetReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun handleIncomingUri(intent: Intent?) {
        val uriFromFileManager: Uri? = intent?.data

        if (uriFromFileManager != null)
            viewModel.addBookPublicationToDatabase(uriFromFileManager, this@MainActivity)
    }

}