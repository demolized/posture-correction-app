package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentProgressBinding
import com.example.myapplication.ui.DrillHistory
import com.example.myapplication.ui.DrillHistoryAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private var drillHistory = mutableListOf<DrillHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.let { binding ->
            loadDrillHistory()
            setupRecyclerView(drillHistory)
            setupChart()
        }
    }

    private fun setupChart() {
        _binding?.let { binding ->
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
    }

    private fun setupRecyclerView(drillHistory: List<DrillHistory>) {
        _binding?.let { binding ->
            val drillHistoryAdapter = DrillHistoryAdapter(drillHistory)
            binding.drillHistoryRecyclerView.adapter = drillHistoryAdapter
            binding.drillHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadDrillHistory() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("drill_history", null)
        val type = object : TypeToken<MutableList<DrillHistory>>() {}.type
        val history = gson.fromJson<MutableList<DrillHistory>>(json, type)
        if (history != null) {
            drillHistory.clear()
            drillHistory.addAll(history)
        }
    }

    private fun saveDrillHistory() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(drillHistory)
        sharedPreferences.edit().putString("drill_history", json).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}