package com.maharashtra.bands.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.databinding.ItemAdminBandBinding


class AdminBandsAdapter(
    private val onEdit: (Band) -> Unit,
    private val onDelete: (Band) -> Unit
) : ListAdapter<Band, AdminBandsAdapter.BandViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BandViewHolder {
        val binding = ItemAdminBandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BandViewHolder(binding, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: BandViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BandViewHolder(
        private val binding: ItemAdminBandBinding,
        private val onEdit: (Band) -> Unit,
        private val onDelete: (Band) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(band: Band) {
            binding.bandName.text = band.name
            binding.bandCity.text = band.city
            binding.bandType.text = band.type
            binding.editButton.setOnClickListener { onEdit(band) }
            binding.deleteButton.setOnClickListener { onDelete(band) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Band>() {
            override fun areItemsTheSame(oldItem: Band, newItem: Band): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Band, newItem: Band): Boolean {
                return oldItem == newItem
            }
        }
    }
}
