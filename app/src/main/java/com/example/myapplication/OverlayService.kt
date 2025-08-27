package com.example.myapplication

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.OverlayLayoutBinding
import com.example.myapplication.ui.PostureViewModel

class OverlayService : Service(), LifecycleOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var binding: OverlayLayoutBinding
    private var isOverlayVisible = false
    private lateinit var postureSensorManager: PostureSensorManager
    private lateinit var postureViewModel: PostureViewModel
    private val lifecycleRegistry = LifecycleRegistry(this)
    private lateinit var vibrator: Vibrator
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModelStoreOwner = ServiceViewModelStoreOwner()

    companion object {
        const val ACTION_SHOW = "com.example.myapplication.ACTION_SHOW"
        const val ACTION_HIDE = "com.example.myapplication.ACTION_HIDE"
        const val OVERLAY_VISIBLE_KEY = "overlay_visible"
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        binding = OverlayLayoutBinding.inflate(LayoutInflater.from(this))
        overlayView = binding.root
        postureViewModel = ViewModelProvider(viewModelStoreOwner).get(PostureViewModel::class.java)
        postureSensorManager = PostureSensorManager(this, postureViewModel)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        sharedPreferences = getSharedPreferences("PostureAppPrefs", Context.MODE_PRIVATE)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        postureViewModel.postureState.observe(this) { postureState ->
            if (postureState == PostureState.GOOD) {
                binding.circle.setImageResource(R.drawable.circle_green)
                vibrator.cancel()
            } else {
                binding.circle.setImageResource(R.drawable.circle_red)
                if (sharedPreferences.getBoolean("vibrate_on_bad_posture", false)) {
                    vibrate()
                }
            }
        }

        postureViewModel.angle.observe(this) { angle ->
            binding.angleTextView.text = angle.toString()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        when (intent?.action) {
            ACTION_SHOW -> showOverlay()
            ACTION_HIDE -> hideOverlay()
            else -> {
                if (sharedPreferences.getBoolean(OVERLAY_VISIBLE_KEY, false)) {
                    showOverlay()
                }
            }
        }
        return START_STICKY
    }

    private fun showOverlay() {
        if (!isOverlayVisible) {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.TOP or Gravity.START
            params.x = 0
            params.y = 100

            windowManager.addView(overlayView, params)
            isOverlayVisible = true
            postureSensorManager.start()
            sharedPreferences.edit().putBoolean(OVERLAY_VISIBLE_KEY, true).apply()
        }
    }

    private fun hideOverlay() {
        if (isOverlayVisible) {
            windowManager.removeView(overlayView)
            isOverlayVisible = false
            postureSensorManager.stop()
            vibrator.cancel()
            sharedPreferences.edit().putBoolean(OVERLAY_VISIBLE_KEY, false).apply()
        }
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 1000, 500), 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 1000, 500), 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlay()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        viewModelStoreOwner.viewModelStore.clear()
    }
}