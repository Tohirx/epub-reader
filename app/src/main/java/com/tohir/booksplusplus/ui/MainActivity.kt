package com.tohir.booksplusplus.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationBarView
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.ActivityMainBinding
import com.tohir.booksplusplus.ui.books.HomeFragment
import com.tohir.booksplusplus.ui.books.LibraryFragment
import kotlinx.coroutines.launch
import java.util.Calendar

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

        maybeRequestExactAlarmPermission()
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

    companion object {
        fun scheduleDailyReset(context: Context) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()
            ) {
                return // Permission missing, do nothing
            }

            val intent = Intent(context, ReadingResetReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
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

    private fun maybeRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            scheduleDailyReset(this)
            return
        }

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (alarmManager.canScheduleExactAlarms()) {
            scheduleDailyReset(this)
            return
        }

        showPermissionAlertDialog()
    }

    private fun showPermissionAlertDialog() {

        val existing = supportFragmentManager.findFragmentByTag("PermissionDialog")
        if (existing != null) return

        val dialog = PermissionDialogFragment().apply {
            onPermissionRequest = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
            onCancel = {
                finish()
            }
        }

        dialog.show(supportFragmentManager, "PermissionDialog")
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleDailyReset(this)
            } else {
                showPermissionAlertDialog()
            }
        }
    }

    private fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            )

        }
    }


}