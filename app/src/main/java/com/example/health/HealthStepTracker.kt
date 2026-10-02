package com.example.health

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.data.model.Habit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HealthStepTracker {

    private const val PREFS_NAME = "health_steps_prefs"
    private const val KEY_TODAY_DATE = "steps_today_date"
    private const val KEY_STEPS_TODAY = "steps_today_count"
    private const val KEY_SENSOR_BOOT_BASELINE = "sensor_boot_baseline"

    private var sensorManager: SensorManager? = null
    private var isListening = false

    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                val totalStepsSinceBoot = event.values.getOrNull(0)?.toInt() ?: return
                handleNewSensorReading(currentContextRef, totalStepsSinceBoot)
            } else if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
                incrementSteps(currentContextRef, 1)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private var currentContextRef: Context? = null

    fun initStepTracking(context: Context) {
        currentContextRef = context.applicationContext
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

        val stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (!isListening) {
            stepCounterSensor?.let {
                sensorManager?.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI)
                isListening = true
                Log.d("HealthStepTracker", "Registered TYPE_STEP_COUNTER")
            } ?: run {
                stepDetectorSensor?.let {
                    sensorManager?.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI)
                    isListening = true
                    Log.d("HealthStepTracker", "Registered TYPE_STEP_DETECTOR")
                }
            }
        }
    }

    fun getTodaySteps(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val todayStr = getTodayDateString()
        val savedDate = prefs.getString(KEY_TODAY_DATE, "")

        return if (savedDate == todayStr) {
            prefs.getInt(KEY_STEPS_TODAY, 0)
        } else {
            prefs.edit()
                .putString(KEY_TODAY_DATE, todayStr)
                .putInt(KEY_STEPS_TODAY, 0)
                .apply()
            0
        }
    }

    fun addSimulationSteps(context: Context, stepsToAdd: Int): Int {
        val current = getTodaySteps(context)
        val newTotal = (current + stepsToAdd).coerceAtLeast(0)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_TODAY_DATE, getTodayDateString())
            .putInt(KEY_STEPS_TODAY, newTotal)
            .apply()
        return newTotal
    }

    private fun incrementSteps(context: Context?, delta: Int) {
        if (context == null) return
        val current = getTodaySteps(context)
        val newTotal = current + delta
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TODAY_DATE, getTodayDateString())
            .putInt(KEY_STEPS_TODAY, newTotal)
            .apply()
    }

    private fun handleNewSensorReading(context: Context?, totalSinceBoot: Int) {
        if (context == null) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val todayStr = getTodayDateString()
        val savedDate = prefs.getString(KEY_TODAY_DATE, "")

        if (savedDate != todayStr) {
            prefs.edit()
                .putString(KEY_TODAY_DATE, todayStr)
                .putInt(KEY_SENSOR_BOOT_BASELINE, totalSinceBoot)
                .putInt(KEY_STEPS_TODAY, 0)
                .apply()
        } else {
            val baseline = prefs.getInt(KEY_SENSOR_BOOT_BASELINE, -1)
            if (baseline == -1 || baseline > totalSinceBoot) {
                prefs.edit().putInt(KEY_SENSOR_BOOT_BASELINE, totalSinceBoot).apply()
            } else {
                val calculatedTodaySteps = totalSinceBoot - baseline
                val currentSaved = prefs.getInt(KEY_STEPS_TODAY, 0)
                if (calculatedTodaySteps > currentSaved) {
                    prefs.edit().putInt(KEY_STEPS_TODAY, calculatedTodaySteps).apply()
                }
            }
        }
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun checkAutoCompletions(
        context: Context,
        todaySteps: Int,
        habits: List<Habit>,
        todayCompletedHabitIds: Set<Long>
    ): List<Habit> {
        val completedList = mutableListOf<Habit>()

        for (habit in habits) {
            val isWalkingOrHealth = habit.isHealthSynced ||
                    habit.name.contains("مشي", ignoreCase = true) ||
                    habit.name.contains("خطوة", ignoreCase = true) ||
                    habit.name.contains("walk", ignoreCase = true) ||
                    habit.name.contains("step", ignoreCase = true)

            if (isWalkingOrHealth && !todayCompletedHabitIds.contains(habit.id)) {
                val target = if (habit.targetSteps > 0) habit.targetSteps else 10000
                if (todaySteps >= target) {
                    completedList.add(habit)
                }
            }
        }

        return completedList
    }
}
