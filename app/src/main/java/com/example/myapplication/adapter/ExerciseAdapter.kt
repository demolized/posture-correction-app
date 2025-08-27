package com.example.myapplication.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Exercise
import com.example.myapplication.databinding.ItemExerciseBinding

class ExerciseAdapter(private val exercises: List<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount() = exercises.size

    inner class ExerciseViewHolder(private val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var timerRunning = false
        private var timeWhenStopped: Long = 0

        fun bind(exercise: Exercise) {
            binding.exerciseNameTextView.text = exercise.name
            binding.exerciseDescriptionTextView.text = exercise.description
            binding.exerciseTipsTextView.text = exercise.tips
            binding.exerciseFrequencyTextView.text = exercise.frequency
            binding.exerciseCheckbox.isChecked = exercise.isCompleted

            binding.root.setOnClickListener {
                binding.expandableDetailsLayout.visibility =
                    if (binding.expandableDetailsLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            binding.playPauseButton.setOnClickListener {
                if (timerRunning) {
                    pauseTimer()
                } else {
                    startTimer()
                }
            }

            binding.resetButton.setOnClickListener {
                resetTimer()
            }
        }

        private fun startTimer() {
            binding.exerciseChronometer.base = SystemClock.elapsedRealtime() - timeWhenStopped
            binding.exerciseChronometer.start()
            binding.playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
            timerRunning = true
        }

        private fun pauseTimer() {
            binding.exerciseChronometer.stop()
            timeWhenStopped = SystemClock.elapsedRealtime() - binding.exerciseChronometer.base
            binding.playPauseButton.setImageResource(android.R.drawable.ic_media_play)
            timerRunning = false
        }

        private fun resetTimer() {
            binding.exerciseChronometer.base = SystemClock.elapsedRealtime()
            timeWhenStopped = 0
            if (!timerRunning) {
                binding.playPauseButton.setImageResource(android.R.drawable.ic_media_play)
            }
        }
    }
}