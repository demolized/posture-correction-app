package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.ExerciseCategory
import com.example.myapplication.databinding.ItemExerciseCategoryBinding

class ExerciseCategoryAdapter(private val categories: List<ExerciseCategory>) :
    RecyclerView.Adapter<ExerciseCategoryAdapter.ExerciseCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseCategoryViewHolder {
        val binding = ItemExerciseCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseCategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class ExerciseCategoryViewHolder(private val binding: ItemExerciseCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: ExerciseCategory) {
            binding.categoryTitleTextView.text = category.name
            binding.exercisesRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.exercisesRecyclerView.adapter = ExerciseAdapter(category.exercises)
        }
    }
}