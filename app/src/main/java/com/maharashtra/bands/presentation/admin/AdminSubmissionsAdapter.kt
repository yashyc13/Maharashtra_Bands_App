package com.maharashtra.bands.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maharashtra.bands.data.model.Submission
import com.maharashtra.bands.databinding.ItemAdminSubmissionBinding


class AdminSubmissionsAdapter(
    private val onApprove: (Submission) -> Unit
) : ListAdapter<Submission, AdminSubmissionsAdapter.SubmissionViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionViewHolder {
        val binding = ItemAdminSubmissionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubmissionViewHolder(binding, onApprove)
    }

    override fun onBindViewHolder(holder: SubmissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SubmissionViewHolder(
        private val binding: ItemAdminSubmissionBinding,
        private val onApprove: (Submission) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(submission: Submission) {
            binding.bandName.text = submission.bandName
            binding.bandCity.text = submission.city
            binding.bandType.text = submission.type
            binding.phoneNumber.text = submission.phoneNumber
            binding.approveButton.setOnClickListener {
                onApprove(submission)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Submission>() {
            override fun areItemsTheSame(oldItem: Submission, newItem: Submission): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Submission, newItem: Submission): Boolean {
                return oldItem == newItem
            }
        }
    }
}
