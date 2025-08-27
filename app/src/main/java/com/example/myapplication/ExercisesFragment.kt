package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.ExerciseCategoryAdapter
import com.example.myapplication.data.Exercise
import com.example.myapplication.data.ExerciseCategory
import com.example.myapplication.data.WeeklyProgram
import com.example.myapplication.databinding.FragmentExercisesBinding

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!

    private val weeklyPrograms = generateSamplePrograms()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weekStrings = weeklyPrograms.map { "Week ${it.weekNumber}: ${it.focus}" }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weekStrings)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.weekSelectorSpinner.adapter = spinnerAdapter

        binding.weekSelectorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateExercises(weeklyPrograms[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        binding.exercisesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        if (weeklyPrograms.isNotEmpty()) {
            updateExercises(weeklyPrograms[0])
        }
    }

    private fun updateExercises(program: WeeklyProgram) {
        binding.exercisesRecyclerView.adapter = ExerciseCategoryAdapter(program.categories)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateSamplePrograms(): List<WeeklyProgram> {
        val exercises1 = listOf(
            Exercise(1, "Cat-Cow Stretch", "Description", "Tips", "Daily"),
            Exercise(2, "Thoracic Spine Windmills", "Description", "Tips", "Daily"),
            Exercise(3, "Neck Retractions", "Description", "Tips", "Daily")
        )
        val category1 = ExerciseCategory(1, "Mobility & Stretching", exercises1)

        val exercises2 = listOf(
            Exercise(4, "Scapular Wall Slides", "Description", "Tips", "3-4x/week"),
            Exercise(5, "Rows", "Description", "Tips", "3-4x/week"),
            Exercise(6, "Band Pull-Aparts", "Description", "Tips", "3-4x/week"),
            Exercise(7, "Face Pulls", "Description", "Tips", "3-4x/week")
        )
        val category2 = ExerciseCategory(2, "Strengthening", exercises2)

        val exercises3 = listOf(
            Exercise(8, "Chin Tucks", "Description", "Tips", "Daily"),
            Exercise(9, "Wall Angels", "Description", "Tips", "Daily")
        )
        val category3 = ExerciseCategory(3, "Neck & Spinal Alignment", exercises3)

        return listOf(
            WeeklyProgram(1, "Foundation", listOf(category1, category2, category3)),
            WeeklyProgram(2, "Building Strength", listOf(category1, category2, category3)),
            WeeklyProgram(3, "Endurance", listOf(category1, category2, category3)),
            WeeklyProgram(4, "Advanced", listOf(category1, category2, category3)),
            WeeklyProgram(5, "Maintenance", listOf(category1, category2, category3)),
            WeeklyProgram(6, "Foundation II", listOf(category1, category2, category3)),
            WeeklyProgram(7, "Building Strength II", listOf(category1, category2, category3)),
            WeeklyProgram(8, "Endurance II", listOf(category1, category2, category3)),
            WeeklyProgram(9, "Advanced II", listOf(category1, category2, category3)),
            WeeklyProgram(10, "Maintenance II", listOf(category1, category2, category3)),
            WeeklyProgram(11, "Expert", listOf(category1, category2, category3)),
            WeeklyProgram(12, "Mastery", listOf(category1, category2, category3))
        )
    }
}