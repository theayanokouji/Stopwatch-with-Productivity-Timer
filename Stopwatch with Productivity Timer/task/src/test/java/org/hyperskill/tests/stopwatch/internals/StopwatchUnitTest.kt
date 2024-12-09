package org.hyperskill.tests.stopwatch.internals

import android.app.Activity
import android.app.AlarmManager
import android.os.Handler
import android.os.SystemClock
import androidx.core.content.getSystemService
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlarmManager

open class StopwatchUnitTest<T: Activity>(clazz: Class<T>) : AbstractUnitTest<T>(clazz)  {
    fun supportForAlarmManager() {
        val alarmManager = activity.getSystemService<AlarmManager>()
        val shadowAlarmManager: ShadowAlarmManager = Shadows.shadowOf(alarmManager)
        shadowAlarmManager.scheduledAlarms.lastOrNull()?.also {
            val operation = it.operation
            val onAlarmListener = it.onAlarmListener
        
            if (operation != null) {
                val pendingIntent = Shadows.shadowOf(operation)
                if (it.triggerAtTime < SystemClock.currentGnssTimeClock().millis()) {
                    operation.intentSender.sendIntent(
                        pendingIntent.savedContext,
                        pendingIntent.requestCode,
                        pendingIntent.savedIntent,
                        null,
                        Handler(activity.mainLooper)
                    )
                }
            } else if (onAlarmListener != null) {
                if (it.triggerAtTime < SystemClock.currentGnssTimeClock().millis()) {
                    onAlarmListener.onAlarm()
                }
            }
        }
    }
}