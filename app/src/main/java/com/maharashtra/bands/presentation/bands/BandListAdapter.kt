package com.maharashtra.bands.presentation.bands

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.databinding.ItemBandBinding


class BandListAdapter : ListAdapter<Band, BandListAdapter.BandViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BandViewHolder {
        val binding = ItemBandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BandViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BandViewHolder(
        private val binding: ItemBandBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(band: Band) {
            binding.bandName.text = band.name
            binding.bandCity.text = band.city
            binding.bandType.text = band.type
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
