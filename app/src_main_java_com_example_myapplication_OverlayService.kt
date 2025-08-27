package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.myapplication.databinding.OverlayLayoutBinding
import com.example.myapplication.ui.PostureViewModel

class OverlayService : Service(), ViewModelStoreOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var binding: OverlayLayoutBinding
    private var isOverlayVisible = false
    private lateinit var postureSensorManager: PostureSensorManager
    private lateinit var postureViewModel: PostureViewModel
    private val viewModelStore = ViewModelStore()

    companion object {
        const val ACTION_SHOW = "com.example.myapplication.ACTION_SHOW"
        const val ACTION_HIDE = "com.example.myapplication.ACTION_HIDE"
    }

    override fun getViewModelStore(): ViewModelStore = viewModelStore

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        binding = OverlayLayoutBinding.inflate(LayoutInflater.from(this))
        overlayView = binding.root
        postureViewModel = ViewModelProvider(this).get(PostureViewModel::class.java)
        postureSensorManager = PostureSensorManager(this, postureViewModel)

        postureViewModel.postureState.observeForever { postureState ->
            if (postureState == PostureState.GOOD) {
                binding.circle.setImageResource(R.drawable.circle_green)
            } else {
                binding.circle.setImageResource(R.drawable.circle_red)
            }
        }

        postureViewModel.angle.observeForever { angle ->
            binding.angleTextView.text = angle.toString()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW -> showOverlay()
            ACTION_HIDE -> hideOverlay()
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
        }
    }

    private fun hideOverlay() {
        if (isOverlayVisible) {
            windowManager.removeView(overlayView)
            isOverlayVisible = false
            postureSensorManager.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlay()
        viewModelStore.clear()
    }
}