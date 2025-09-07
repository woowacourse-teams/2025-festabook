package com.daedan.festabook.presentation.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.daedan.festabook.BuildConfig
import com.daedan.festabook.R
import com.daedan.festabook.databinding.FragmentSettingBinding
import com.daedan.festabook.presentation.NotificationPermissionManager
import com.daedan.festabook.presentation.NotificationPermissionRequester
import com.daedan.festabook.presentation.common.BaseFragment
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.common.showNotificationDeniedSnackbar
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
            onPermissionGranted = { onPermissionGranted() },
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
                showNotificationDeniedSnackbar(binding.root, requireContext())
                binding.btnNoticeAllow.isChecked = false
                viewModel.updateNotificationIsAllowed(false)
                viewModel.saveNotificationIsAllowed(false)
            }
        }

    override fun onPermissionGranted() = Unit

    override fun onPermissionDenied() = Unit

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupBindings()

        setupNoticeAllowButtonClickListener()
        setupServicePolicyClickListener()
        setupContactUsButtonClickListener()

        setupObservers()
    }

    private fun setupBindings() {
        binding.btnNoticeAllow.isChecked = viewModel.isAllowed
        val versionName = BuildConfig.VERSION_NAME
        binding.tvSettingAppVersionName.text = versionName
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
            val intent = Intent(Intent.ACTION_VIEW, CONTACT_US_URL.toUri())
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

        private const val CONTACT_US_URL =
            "https://forms.gle/XjqJFfQrTPgkZzGZ9"
    }
}
