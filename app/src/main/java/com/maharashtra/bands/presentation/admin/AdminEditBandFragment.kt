package com.maharashtra.bands.presentation.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maharashtra.bands.R
import com.maharashtra.bands.data.model.Band
import com.maharashtra.bands.databinding.FragmentAdminEditBandBinding


class AdminEditBandFragment : Fragment(R.layout.fragment_admin_edit_band) {
    private var _binding: FragmentAdminEditBandBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminEditBandViewModel by viewModels()

    private lateinit var band: Band

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminEditBandBinding.bind(view)

        band = BandEditArgs.from(arguments)
        bindForm(band)

        binding.saveButton.setOnClickListener {
            updateBand()
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindForm(band: Band) {
        binding.nameInput.setText(band.name)
        binding.cityInput.setText(band.city)
        binding.typeInput.setText(band.type)
        binding.descriptionInput.setText(band.description)
    }

    private fun updateBand() {
        val name = binding.nameInput.text?.toString().orEmpty().trim()
        val city = binding.cityInput.text?.toString().orEmpty().trim()
        val type = binding.typeInput.text?.toString().orEmpty().trim()
        val description = binding.descriptionInput.text?.toString().orEmpty().trim()

        if (name.isBlank()) {
            binding.nameInputLayout.error = getString(R.string.admin_edit_error_name)
            return
        } else {
            binding.nameInputLayout.error = null
        }

        if (city.isBlank()) {
            binding.cityInputLayout.error = getString(R.string.admin_edit_error_city)
            return
        } else {
            binding.cityInputLayout.error = null
        }

        if (type.isBlank()) {
            binding.typeInputLayout.error = getString(R.string.admin_edit_error_type)
            return
        } else {
            binding.typeInputLayout.error = null
        }

        val updated = band.copy(
            name = name,
            city = city,
            type = type,
            description = description
        )
        viewModel.updateBand(updated)
    }

    private fun observeViewModel() {
        viewModel.isSaving.observe(viewLifecycleOwner) { saving ->
            binding.saveButton.isEnabled = !saving
            binding.progressBar.visibility = if (saving) View.VISIBLE else View.GONE
        }

        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), R.string.admin_edit_success, Toast.LENGTH_SHORT).show()
                viewModel.markSaveHandled()
                parentFragmentManager.popBackStack()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(band: Band): AdminEditBandFragment {
            val fragment = AdminEditBandFragment()
            fragment.arguments = BandEditArgs.toBundle(band)
            return fragment
        }
    }
}

private object BandEditArgs {
    private const val KEY_ID = "band_id"
    private const val KEY_NAME = "band_name"
    private const val KEY_CITY = "band_city"
    private const val KEY_TYPE = "band_type"
    private const val KEY_DESCRIPTION = "band_description"
    private const val KEY_IMAGE_URL = "band_image_url"
    private const val KEY_IS_APPROVED = "band_is_approved"

    fun toBundle(band: Band): Bundle {
        return Bundle().apply {
            putString(KEY_ID, band.id)
            putString(KEY_NAME, band.name)
            putString(KEY_CITY, band.city)
            putString(KEY_TYPE, band.type)
            putString(KEY_DESCRIPTION, band.description)
            putString(KEY_IMAGE_URL, band.imageUrl)
            putBoolean(KEY_IS_APPROVED, band.isApproved)
        }
    }

    fun from(bundle: Bundle?): Band {
        return Band(
            id = bundle?.getString(KEY_ID).orEmpty(),
            name = bundle?.getString(KEY_NAME).orEmpty(),
            city = bundle?.getString(KEY_CITY).orEmpty(),
            type = bundle?.getString(KEY_TYPE).orEmpty(),
            description = bundle?.getString(KEY_DESCRIPTION).orEmpty(),
            imageUrl = bundle?.getString(KEY_IMAGE_URL).orEmpty(),
            isApproved = bundle?.getBoolean(KEY_IS_APPROVED) ?: true
        )
    }
}
