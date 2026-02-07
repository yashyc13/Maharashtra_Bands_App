package com.maharashtra.bands.presentation.submit

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maharashtra.bands.R
import com.maharashtra.bands.databinding.FragmentSubmitBandBinding


class SubmitBandFragment : Fragment(R.layout.fragment_submit_band) {
    private var _binding: FragmentSubmitBandBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubmitBandViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        binding.imageStatus.text = if (uri == null) {
            getString(R.string.submit_band_image_none)
        } else {
            getString(R.string.submit_band_image_selected)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSubmitBandBinding.bind(view)

        binding.uploadButton.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.submitButton.setOnClickListener {
            submitForm()
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.isSubmitting.observe(viewLifecycleOwner) { submitting ->
            binding.submitButton.isEnabled = !submitting
            binding.progressBar.visibility = if (submitting) View.VISIBLE else View.GONE
        }
        viewModel.submitSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                showMessage(getString(R.string.submit_band_success))
                clearForm()
                viewModel.markSuccessHandled()
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                showMessage(message)
            }
        }
    }

    private fun submitForm() {
        val name = binding.nameInput.text?.toString().orEmpty().trim()
        val city = binding.cityInput.text?.toString().orEmpty().trim()
        val type = binding.typeInput.text?.toString().orEmpty().trim()
        val phone = binding.phoneInput.text?.toString().orEmpty().trim()

        if (name.isBlank()) {
            binding.nameInputLayout.error = getString(R.string.submit_band_error_name)
            return
        } else {
            binding.nameInputLayout.error = null
        }

        if (city.isBlank()) {
            binding.cityInputLayout.error = getString(R.string.submit_band_error_city)
            return
        } else {
            binding.cityInputLayout.error = null
        }

        if (type.isBlank()) {
            binding.typeInputLayout.error = getString(R.string.submit_band_error_type)
            return
        } else {
            binding.typeInputLayout.error = null
        }

        if (phone.isBlank()) {
            binding.phoneInputLayout.error = getString(R.string.submit_band_error_phone)
            return
        } else {
            binding.phoneInputLayout.error = null
        }

        viewModel.submitBand(
            name = name,
            city = city,
            type = type,
            phoneNumber = phone,
            imageUri = selectedImageUri
        )
    }

    private fun clearForm() {
        binding.nameInput.text = null
        binding.cityInput.text = null
        binding.typeInput.text = null
        binding.phoneInput.text = null
        selectedImageUri = null
        binding.imageStatus.text = getString(R.string.submit_band_image_none)
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
