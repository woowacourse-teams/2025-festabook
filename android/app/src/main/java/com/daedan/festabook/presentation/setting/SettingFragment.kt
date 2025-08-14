package com.daedan.festabook.presentation.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentSettingBinding
import com.daedan.festabook.presentation.NotificationPermissionManager
import com.daedan.festabook.presentation.NotificationPermissionRequester
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import timber.log.Timber

class SettingFragment :
    BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting),
    NotificationPermissionRequester {
    private val viewModel: SettingViewModel by viewModels {
        SettingViewModel.factory()
    }

    private val notificationPermissionManager by lazy {
        NotificationPermissionManager(
            requester = this,
            onPermissionDenied = { onPermissionDenied() },
        )
    }

    override val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                Timber.d("Notification permission granted")
            } else {
                Timber.d("Notification permission denied")
                // 사용자에게 알림 권한이 필요한 이유를 설명하거나, 설정 화면으로 유도
            }
        }

    override fun onPermissionGranted() = Unit

    override fun onPermissionDenied() {
        binding.btnNoticeAllow.isChecked = false
        viewModel.updateNotificationIsAllowed(false)
        viewModel.saveNotificationIsAllowed(false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNoticeAllow.isChecked = viewModel.isAllowed
        setupNoticeAllowButtonClickListener()
        setupServicePolicyClickListener()
        setupContactUsButtonClickListener()

        setupObservers()
    }

    override fun shouldShowPermissionRationale(permission: String): Boolean = shouldShowRequestPermissionRationale(permission)

    private fun setupObservers() {
        viewModel.allowClickEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (viewModel.isAllowed) {
                    notificationPermissionManager.requestNotificationPermission(
                        requireContext(),
                    )
                }
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { throwable ->
                showErrorSnackBar(throwable)
            }
        }
    }

    private fun setupServicePolicyClickListener() {
        binding.tvSettingServicePolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, POLICY_URL.toUri())
            startActivity(intent)
        }
    }

    private fun setupContactUsButtonClickListener() {
        binding.tvSettingContactUs.setOnClickListener {
            val emailAddress = getString(R.string.setting_contact_us_email)
            val subject = R.string.setting_contact_us_email_subject

            val uri = "mailto:$emailAddress?subject=${Uri.encode(subject.toString())}".toUri()
            val intent = Intent(Intent.ACTION_SENDTO, uri)

            startActivity(intent)
        }
    }

    private fun setupNoticeAllowButtonClickListener() {
        binding.btnNoticeAllow.setOnClickListener {
            viewModel.notificationAllowClick()
        }
    }

    companion object {
        private const val POLICY_URL: String =
            "https://www.notion.so/244a540dc0b780638e56e31c4bdb3c9f"
    }
}
