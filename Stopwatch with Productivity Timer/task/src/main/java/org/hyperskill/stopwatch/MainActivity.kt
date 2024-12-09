package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.Notification.FLAG_INSISTENT
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // get the views
        val btnStart = findViewById<Button>(R.id.startButton)
        val btnReset = findViewById<Button>(R.id.resetButton)
        val txtTime = findViewById<TextView>(R.id.textView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val settingButton = findViewById<Button>(R.id.settingsButton)
        var s = 0L // represents seconds
        var m = 0 // represents minutes
        var left = ""
        var right = ""
        var counter = 0
        val millisInFuture = 3.54e+6.toLong()
        var milliSeconds = 0L
        var randInt: Int // will store a random number for the color I choose
        var isRunning = false
        val layout = LayoutInflater.from(this@MainActivity).inflate(R.layout.activity_dialog, null)
        val editText = layout.findViewById<EditText>(R.id.upperLimitEditText)
        // create an AlertDialog
        val builder = AlertDialog.Builder(this@MainActivity)
            .setMessage(ContextCompat.getString(this@MainActivity, R.string.alertDialogMessage))
            .setView(layout)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // update the milliseconds to this number of seconds received from the user
                val seconds = editText.text.toString().toLong()
                // now convert seconds to milliseconds
                milliSeconds = seconds * 1000L
                // now check if the milliseconds don't exceed the expected maximum of 59 seconds
                if (milliSeconds > millisInFuture) {
                    Toast.makeText(
                        this@MainActivity,
                        ContextCompat.getString(this@MainActivity, R.string.wrongUpperLimit),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss() // close dialog
            }
            .create()

        // create a notification channel
        val channelId = "org.hyperskill"
        val channelName = "Stopwatch"
        val channel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            // create the notification channel
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // handle when buttons are clicked
        btnStart.setOnClickListener {
            // make the progress bar visible
            if (!isRunning) isRunning = true
            progressBar.visibility = ProgressBar.VISIBLE
            counter++
            if (counter == 1 && isRunning) {
                // disable the settingsButton
                settingButton.isEnabled = false
                timer = object : CountDownTimer(millisInFuture, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (milliSeconds != 0L) {
                            if (s == milliSeconds / 1000L) {
                                txtTime.setTextColor(Color.RED)
                                // add an android notification
                                val notification = NotificationCompat.Builder(this@MainActivity, channelId)
                                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                                    .setContentTitle(ContextCompat.getString(this@MainActivity, R.string.notificationTitle))
                                    .setContentText(ContextCompat.getString(this@MainActivity, R.string.notificationText))
                                    .setOnlyAlertOnce(true)

                                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                val notify = notification.build()
                                notify.flags = FLAG_INSISTENT or notify.flags
                                manager.notify(393939, notify)
                            }
                        }
                        // choose a random color
                        randInt = Random.nextInt()
                        // now update the color
                        progressBar.indeterminateTintList = ColorStateList.valueOf(randInt)
                        // handle time display
                        if (s == 60L) {
                            m++
                            s = 0
                        }
                        left = if (m.toString().length < 2) "0$m" else "$m"
                        right = if (s.toString().length < 2) "0$s" else "$s"
                        txtTime.text = "$left:$right"
                        s++
                    }

                    override fun onFinish() {
                    }
                }.start()
            }
        }

        btnReset.setOnClickListener {
            progressBar.visibility = ProgressBar.INVISIBLE
            settingButton.isEnabled = true // enable settings button when the timer is not running
            isRunning = false
            timer.cancel()
            txtTime.text = getString(R.string.timer)
            counter = 0
            s = 0
            m = 0
            txtTime.setTextColor(Color.BLACK) // set the color back to its default color
        }

        settingButton.setOnClickListener {
            if (!isRunning) {
                builder.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}