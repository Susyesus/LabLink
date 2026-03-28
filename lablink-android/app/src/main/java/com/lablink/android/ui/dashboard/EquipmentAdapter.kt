package com.lablink.android.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lablink.android.R
import com.lablink.android.data.model.EquipmentDto
import com.lablink.android.databinding.ItemEquipmentBinding

class EquipmentAdapter(
    private val onItemClick: (EquipmentDto) -> Unit
) : ListAdapter<EquipmentDto, EquipmentAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEquipmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemEquipmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EquipmentDto) {
            binding.tvEquipmentName.text = item.name
            binding.tvCategory.text = item.category?.name ?: "Uncategorized"
            binding.tvDescription.text = item.description ?: "No description"

            // Status badge with correct colors
            when (item.status) {
                "AVAILABLE" -> {
                    binding.tvStatus.text = "Available"
                    binding.tvStatus.setBackgroundResource(R.drawable.badge_available)
                    binding.tvStatus.setTextColor(0xFF10B981.toInt())
                }
                "UNAVAILABLE", "IN_USE" -> {
                    binding.tvStatus.text = "In Use"
                    binding.tvStatus.setBackgroundResource(R.drawable.badge_unavailable)
                    binding.tvStatus.setTextColor(0xFFEF4444.toInt())
                }
                "MAINTENANCE" -> {
                    binding.tvStatus.text = "Maint."
                    binding.tvStatus.setBackgroundResource(R.drawable.badge_maintenance)
                    binding.tvStatus.setTextColor(0xFFF59E0B.toInt())
                }
                else -> {
                    binding.tvStatus.text = item.status
                    binding.tvStatus.setBackgroundResource(R.drawable.badge_available)
                    binding.tvStatus.setTextColor(0xFF10B981.toInt())
                }
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<EquipmentDto>() {
        override fun areItemsTheSame(oldItem: EquipmentDto, newItem: EquipmentDto) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EquipmentDto, newItem: EquipmentDto) =
            oldItem == newItem
    }
}
