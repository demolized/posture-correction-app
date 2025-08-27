package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemDrillHistoryBinding

class DrillHistoryAdapter(private val drillHistory: List<DrillHistory>) :
    RecyclerView.Adapter<DrillHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDrillHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = drillHistory[position]
        holder.binding.dateText.text = item.date
        holder.binding.durationText.text = item.duration
        holder.binding.accuracyText.text = item.accuracy
    }

    override fun getItemCount(): Int {
        return drillHistory.size
    }

    class ViewHolder(val binding: ItemDrillHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}