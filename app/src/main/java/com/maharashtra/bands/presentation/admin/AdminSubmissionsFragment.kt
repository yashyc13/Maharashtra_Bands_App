package com.maharashtra.bands.presentation.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maharashtra.bands.R
import com.maharashtra.bands.databinding.FragmentAdminSubmissionsBinding


class AdminSubmissionsFragment : Fragment(R.layout.fragment_admin_submissions) {
    private var _binding: FragmentAdminSubmissionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminSubmissionsViewModel by viewModels()
    private val adapter = AdminSubmissionsAdapter { submission ->
        viewModel.approveSubmission(submission)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminSubmissionsBinding.bind(view)

        binding.submissionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.submissionsRecyclerView.adapter = adapter

        observeViewModel()
        viewModel.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.submissions.observe(viewLifecycleOwner) { submissions ->
            adapter.submitList(submissions)
            binding.emptyState.visibility = if (submissions.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
