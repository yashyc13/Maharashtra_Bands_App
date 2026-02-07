package com.maharashtra.bands.presentation.bandprofile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.maharashtra.bands.R
import com.maharashtra.bands.databinding.FragmentBandProfileBinding


class BandProfileFragment : Fragment(R.layout.fragment_band_profile) {
    private var _binding: FragmentBandProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBandProfileBinding.bind(view)

        val details = BandProfileArgs.from(arguments)
        bindDetails(details)

        binding.callButton.setOnClickListener {
            openDialer(details.phoneNumber)
        }

        binding.whatsappButton.setOnClickListener {
            openWhatsApp(details.phoneNumber)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindDetails(details: BandProfileArgs) {
        binding.bandName.text = details.name
        binding.bandCity.text = details.city
        binding.bandType.text = details.type
        binding.bandDescription.text = details.description
        binding.callButton.isEnabled = details.phoneNumber.isNotBlank()
        binding.whatsappButton.isEnabled = details.phoneNumber.isNotBlank()
    }

    private fun openDialer(phoneNumber: String) {
        if (phoneNumber.isBlank()) {
            showMessage(getString(R.string.band_profile_no_phone))
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    private fun openWhatsApp(phoneNumber: String) {
        if (phoneNumber.isBlank()) {
            showMessage(getString(R.string.band_profile_no_phone))
            return
        }
        val sanitized = phoneNumber.filter { it.isDigit() || it == '+' }
        val uri = Uri.parse("https://wa.me/$sanitized")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage(WHATSAPP_PACKAGE)
        }

        try {
            startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            showMessage(getString(R.string.band_profile_whatsapp_missing))
            val storeIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$WHATSAPP_PACKAGE")
            )
            try {
                startActivity(storeIntent)
            } catch (_: ActivityNotFoundException) {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$WHATSAPP_PACKAGE")
                )
                startActivity(webIntent)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
    }
}


data class BandProfileArgs(
    val name: String,
    val city: String,
    val type: String,
    val description: String,
    val phoneNumber: String
) {
    companion object {
        fun from(bundle: Bundle?): BandProfileArgs {
            return BandProfileArgs(
                name = bundle?.getString(KEY_NAME).orEmpty(),
                city = bundle?.getString(KEY_CITY).orEmpty(),
                type = bundle?.getString(KEY_TYPE).orEmpty(),
                description = bundle?.getString(KEY_DESCRIPTION).orEmpty(),
                phoneNumber = bundle?.getString(KEY_PHONE).orEmpty()
            )
        }

        const val KEY_NAME = "band_name"
        const val KEY_CITY = "band_city"
        const val KEY_TYPE = "band_type"
        const val KEY_DESCRIPTION = "band_description"
        const val KEY_PHONE = "band_phone"
    }
}
