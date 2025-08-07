package com.daedan.festabook.presentation.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentSettingBinding
import com.daedan.festabook.presentation.common.BaseFragment

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupServicePolicyClickListener()
        setupContactUsButtonClickListener()
    }

    private fun setupServicePolicyClickListener() {
        binding.ivSettingServicePolicy.setOnClickListener {
            val url = "https://www.notion.so/244a540dc0b780638e56e31c4bdb3c9f"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
    }

    private fun setupContactUsButtonClickListener() {
        binding.ivSettingContactUs.setOnClickListener {
            val emailAddress = "festabook2025@gmail.com"
            val subject = "문의사항"

            val uri = "mailto:$emailAddress?subject=${Uri.encode(subject)}".toUri()
            val intent = Intent(Intent.ACTION_SENDTO, uri)

            startActivity(intent)
        }
    }
}
