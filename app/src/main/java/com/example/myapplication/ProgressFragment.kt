package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentProgressBinding
import com.example.myapplication.ui.DrillHistory
import com.example.myapplication.ui.DrillHistoryAdapter
import com.example.myapplication.ui.PostureViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private var drillHistory = mutableListOf<DrillHistory>()
    private val postureViewModel: PostureViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDrillHistory()
        setupRecyclerView(drillHistory)
        setupChart()

        postureViewModel.angle.observe(viewLifecycleOwner) { angle ->
            binding.angleTextView.text = getString(R.string.angle_format, angle)
        }

        postureViewModel.goodPostureTime.observe(viewLifecycleOwner) { time ->
            binding.goodPostureTimerTextView.text = formatTime(time)
        }

        postureViewModel.badPostureTime.observe(viewLifecycleOwner) { time ->
            binding.badPostureTimerTextView.text = formatTime(time)
        }
    }

    private fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupChart() {
        val entries = ArrayList<Entry>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for ((index, history) in drillHistory.withIndex()) {
            try {
                val date = dateFormat.parse(history.date)
                val accuracy = history.accuracy.replace("%", "").toFloat()
                if (date != null) {
                    entries.add(Entry(date.time.toFloat(), accuracy))
                }
            } catch (e: Exception) {
                // Ignore entries with invalid date or accuracy formats
            }
        }

        val dataSet = LineDataSet(entries, "Accuracy")
        val lineData = LineData(dataSet)
        binding.streakChart.data = lineData
        binding.streakChart.invalidate()
    }

    private fun setupRecyclerView(drillHistory: List<DrillHistory>) {
        val drillHistoryAdapter = DrillHistoryAdapter(drillHistory)
        binding.drillHistoryRecyclerView.adapter = drillHistoryAdapter
        binding.drillHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadDrillHistory() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        // Clear mock data
        sharedPreferences.edit().remove("drill_history").apply()
        val gson = Gson()
        val json = sharedPreferences.getString("drill_history", null)
        val type = object : TypeToken<MutableList<DrillHistory>>() {}.type
        val history = gson.fromJson<MutableList<DrillHistory>>(json, type)
        if (history != null) {
            drillHistory.clear()
            drillHistory.addAll(history)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}