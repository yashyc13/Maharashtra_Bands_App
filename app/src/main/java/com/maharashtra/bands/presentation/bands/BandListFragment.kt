package com.maharashtra.bands.presentation.bands

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maharashtra.bands.R
import com.maharashtra.bands.databinding.FragmentBandListBinding


class BandListFragment : Fragment(R.layout.fragment_band_list) {
    private var _binding: FragmentBandListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BandListViewModel by viewModels()
    private val adapter = BandListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBandListBinding.bind(view)

        setupRecyclerView()
        observeViewModel()

        if (savedInstanceState == null) {
            viewModel.loadInitial()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.bandsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bandsRecyclerView.adapter = adapter
        binding.bandsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val threshold = 5
                if (totalItemCount <= lastVisibleItem + threshold) {
                    viewModel.fetchNextPage()
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.bands.observe(viewLifecycleOwner) { bands ->
            adapter.submitList(bands)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            binding.errorState.visibility = if (message.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.errorState.text = message
        }
    }
}
