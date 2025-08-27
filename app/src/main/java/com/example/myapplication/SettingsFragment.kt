package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.myapplication.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(requireContext())) {
            startOverlayService()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        binding.calibrateButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_nav_calibration)
        }

        binding.startSessionButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_settings_to_nav_posture)
        }

        binding.sensitivitySlider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle sensitivity slider progress change
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                // Handle sensitivity slider start tracking touch
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                // Handle sensitivity slider stop tracking touch
            }
        })

        binding.toleranceSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.toleranceText.text = "Tolerance: ${progress}°"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                sharedPreferences.edit {
                    putInt("posture_tolerance", seekBar?.progress ?: 10)
                }
            }
        })

        binding.overlayModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAndShowOverlay()
            } else {
                stopOverlayService()
            }
        }

        // Restore the saved tolerance value
        val savedTolerance = sharedPreferences.getInt("posture_tolerance", 10)
        binding.toleranceSlider.progress = savedTolerance
        binding.toleranceText.text = "Tolerance: ${savedTolerance}°"
    }

    private fun checkAndShowOverlay() {
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