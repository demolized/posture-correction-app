package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentPostureBinding
import com.example.myapplication.ui.PostureViewModel

class PostureFragment : Fragment() {

    private var _binding: FragmentPostureBinding? = null
    private val binding get() = _binding!!

    private val postureViewModel: PostureViewModel by activityViewModels()
    private lateinit var postureSensorManager: PostureSensorManager
    private lateinit var sharedPreferences: SharedPreferences

    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(requireContext())) {
            startOverlayService()
        }
    }

    private var timerRunning = false
    private var timeWhenStopped: Long = 0
    private var goodPostureTime: Long = 0
    private var badPostureTime: Long = 0
    private var lastTick: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postureSensorManager = PostureSensorManager(requireContext(), postureViewModel)
        sharedPreferences = requireContext().getSharedPreferences("PostureAppPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.trackingSwitch.isChecked = sharedPreferences.getBoolean("tracking_on", false)
        binding.trackingSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit { putBoolean("tracking_on", isChecked) }
            if (isChecked) {
                postureSensorManager.start()
            } else {
                postureSensorManager.stop()
            }
        }

        binding.vibrateSwitch.isChecked = sharedPreferences.getBoolean("vibrate_on_bad_posture", false)
        binding.vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit { putBoolean("vibrate_on_bad_posture", isChecked) }
        }
        
        binding.overlaySwitch.isChecked = sharedPreferences.getBoolean(OverlayService.OVERLAY_VISIBLE_KEY, false)
        binding.overlaySwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit { putBoolean(OverlayService.OVERLAY_VISIBLE_KEY, isChecked) }
            if (isChecked) {
                checkAndStartOverlayService()
            } else {
                stopOverlayService()
            }
        }

        postureViewModel.postureState.observe(viewLifecycleOwner) { postureState ->
            val colorRes = if (postureState == PostureState.GOOD) R.color.green else R.color.red
            binding.postureProgress.setIndicatorColor(ContextCompat.getColor(requireContext(), colorRes))
        }

        postureViewModel.angle.observe(viewLifecycleOwner) { angle ->
            binding.angleTextView.text = getString(R.string.angle_format, angle)
            binding.postureProgress.progress = angle
        }

        binding.startButton.setOnClickListener {
            startTimer()
        }

        binding.stopButton.setOnClickListener {
            stopTimer()
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }

        binding.timerChronometer.setOnChronometerTickListener {
            val elapsedMillis = SystemClock.elapsedRealtime() - it.base
            if (lastTick != 0L) {
                val delta = elapsedMillis - lastTick
                if (postureViewModel.postureState.value == PostureState.GOOD) {
                    goodPostureTime += delta
                } else {
                    badPostureTime += delta
                }
                postureViewModel.setGoodPostureTime(goodPostureTime)
                postureViewModel.setBadPostureTime(badPostureTime)
            }
            lastTick = elapsedMillis
        }
    }

    private fun startTimer() {
        if (!timerRunning) {
            binding.timerChronometer.base = SystemClock.elapsedRealtime() - timeWhenStopped
            binding.timerChronometer.start()
            timerRunning = true
            lastTick = 0
        }
    }

    private fun stopTimer() {
        if (timerRunning) {
            binding.timerChronometer.stop()
            timeWhenStopped = SystemClock.elapsedRealtime() - binding.timerChronometer.base
            timerRunning = false
        }
    }

    private fun resetTimer() {
        binding.timerChronometer.base = SystemClock.elapsedRealtime()
        timeWhenStopped = 0
        goodPostureTime = 0
        badPostureTime = 0
        lastTick = 0
        postureViewModel.setGoodPostureTime(goodPostureTime)
        postureViewModel.setBadPostureTime(badPostureTime)
        if (!timerRunning) {
            binding.timerChronometer.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferences.getBoolean("tracking_on", false)) {
            postureSensorManager.start()
        }
    }

    override fun onPause() {
        super.onPause()
        postureSensorManager.stop()
        if (timerRunning) {
            stopTimer()
        }
    }

    private fun checkAndStartOverlayService() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${requireContext().packageName}".toUri()
            )
            overlayPermissionLauncher.launch(intent)
        } else {
            startOverlayService()
        }
    }

    private fun startOverlayService() {
        val intent = Intent(requireContext(), OverlayService::class.java).apply {
            action = OverlayService.ACTION_SHOW
        }
        requireContext().startService(intent)
    }



    private fun stopOverlayService() {
        val intent = Intent(requireContext(), OverlayService::class.java).apply {
            action = OverlayService.ACTION_HIDE
        }
        requireContext().startService(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}