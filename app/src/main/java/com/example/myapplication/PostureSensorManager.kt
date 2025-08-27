package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.preference.PreferenceManager
import com.example.myapplication.ui.PostureViewModel
import kotlin.math.atan2
import kotlin.math.sqrt

enum class PostureState {
    GOOD,
    BAD
}

class PostureSensorManager(
    context: Context,
    private val postureViewModel: PostureViewModel
) : SensorEventListener {

    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    var currentAngle: Int = 0
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val angle = calculateAngle(x, y, z)
            currentAngle = angle.toInt()
            val postureState = determinePostureState(angle)

            postureViewModel.updatePosture(postureState, currentAngle)
        }
    }

    private fun determinePostureState(angle: Double): PostureState {
        val minAngle = sharedPreferences.getInt("min_good_posture_angle", 70)
        val maxAngle = sharedPreferences.getInt("max_good_posture_angle", 90)
        return if (angle >= minAngle && angle <= maxAngle) {
            PostureState.GOOD
        } else {
            PostureState.BAD
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    fun calculateAngle(x: Float, y: Float, z: Float): Double {
        // Calculate the pitch of the device. This gives us the angle of the device's tilt.
        val norm = sqrt(x * x + y * y + z * z)
        val pitch = Math.asin((y / norm).toDouble())
        return Math.toDegrees(pitch)
    }
}