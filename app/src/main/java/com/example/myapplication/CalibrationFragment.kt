package com.example.myapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.example.myapplication.databinding.FragmentCalibrationBinding
import com.example.myapplication.ui.PostureViewModel

class CalibrationFragment : Fragment() {

    private var _binding: FragmentCalibrationBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val postureViewModel: PostureViewModel by activityViewModels()
    private lateinit var postureSensorManager: PostureSensorManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalibrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        postureSensorManager = PostureSensorManager(requireContext(), postureViewModel)

        binding.setGoodPostureButton.setOnClickListener {
            val currentAngle = postureViewModel.angle.value ?: 0
            val tolerance = sharedPreferences.getInt("posture_tolerance", 10)
            val minAngle = currentAngle - tolerance
            val maxAngle = currentAngle + tolerance
            sharedPreferences.edit {
                putInt("min_good_posture_angle", minAngle)
                putInt("max_good_posture_angle", maxAngle)
            }
            updateSavedAngleRangeText()
            Toast.makeText(requireContext(), "Good posture range saved", Toast.LENGTH_SHORT).show()
        }

        postureViewModel.angle.observe(viewLifecycleOwner) { angle ->
            binding.currentAngleText.text = "Current Angle: $angle°"
        }

        updateSavedAngleRangeText()
    }

    override fun onResume() {
        super.onResume()
        postureSensorManager.start()
    }

    override fun onPause() {
        super.onPause()
        postureSensorManager.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateSavedAngleRangeText() {
        val minAngle = sharedPreferences.getInt("min_good_posture_angle", 70)
        val maxAngle = sharedPreferences.getInt("max_good_posture_angle", 90)
        binding.savedAngleRangeText.text = "Saved Range: $minAngle°-$maxAngle°"
    }
}