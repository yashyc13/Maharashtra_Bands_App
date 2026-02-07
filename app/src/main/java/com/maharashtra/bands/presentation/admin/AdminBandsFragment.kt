package com.maharashtra.bands.presentation.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maharashtra.bands.R
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.databinding.FragmentAdminBandsBinding


class AdminBandsFragment : Fragment(R.layout.fragment_admin_bands) {
    private var _binding: FragmentAdminBandsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminBandsViewModel by viewModels()

    private val adapter = AdminBandsAdapter(
        onEdit = { band ->
            openEdit(band)
        },
        onDelete = { band ->
            viewModel.deleteBand(band.id)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminBandsBinding.bind(view)

        binding.bandsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bandsRecyclerView.adapter = adapter

        observeViewModel()
        viewModel.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.bands.observe(viewLifecycleOwner) { bands ->
            adapter.submitList(bands)
            binding.emptyState.visibility = if (bands.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openEdit(band: Band) {
        val fragment = AdminEditBandFragment.newInstance(band)
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
