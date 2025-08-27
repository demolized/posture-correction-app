package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(requireContext())) {
                startOverlayService()
            }
        }
    }

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
            sharedPreferences.edit().putBoolean("tracking_on", isChecked).apply()
            if (isChecked) {
                postureSensorManager.start()
            } else {
                postureSensorManager.stop()
            }
        }

        binding.vibrateSwitch.isChecked = sharedPreferences.getBoolean("vibrate_on_bad_posture", false)
        binding.vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("vibrate_on_bad_posture", isChecked).apply()
        }
        
        binding.overlaySwitch.isChecked = sharedPreferences.getBoolean(OverlayService.OVERLAY_VISIBLE_KEY, false)
        binding.overlaySwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(OverlayService.OVERLAY_VISIBLE_KEY, isChecked).apply()
            if (isChecked) {
                checkAndStartOverlayService()
            } else {
                stopOverlayService()
            }
        }

        postureViewModel.postureState.observe(viewLifecycleOwner) { postureState ->
            if (postureState == PostureState.GOOD) {
                binding.circle.setImageResource(R.drawable.circle_green)
            } else {
                binding.circle.setImageResource(R.drawable.circle_red)
            }
        }

        postureViewModel.angle.observe(viewLifecycleOwner) { angle ->
            binding.angleTextView.text = "Angle: $angle°"
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
    }

    private fun checkAndStartOverlayService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
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