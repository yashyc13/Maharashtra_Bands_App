package com.maharashtra.bands.presentation.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maharashtra.bands.R
import com.maharashtra.bands.databinding.FragmentAdminLoginBinding


class AdminLoginFragment : Fragment(R.layout.fragment_admin_login) {
    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminLoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminLoginBinding.bind(view)

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text?.toString().orEmpty().trim()
            val password = binding.passwordInput.text?.toString().orEmpty()
            viewModel.login(email, password)
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.loginButton.isEnabled = !loading
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                showMessage(getString(R.string.admin_login_success))
                viewModel.markLoginHandled()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                showMessage(message)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
