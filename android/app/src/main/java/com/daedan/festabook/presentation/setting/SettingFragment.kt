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
import com.daedan.festabook.presentation.common.showSnackBar
import com.daedan.festabook.presentation.home.HomeViewModel
import com.daedan.festabook.presentation.home.adapter.FestivalUiState
import timber.log.Timber

class SettingFragment :
    BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting),
    NotificationPermissionRequester {
    private val settingViewModel: SettingViewModel by viewModels({ requireActivity() }) {
        SettingViewModel.factory()
    }

    private val homeViewModel: HomeViewModel by viewModels({ requireActivity() }) {
        HomeViewModel.Factory
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
                onPermissionGranted()
            } else {
                Timber.d("Notification permission denied")
                showNotificationDeniedSnackbar(binding.root, requireContext())
                onPermissionDenied()
            }
        }

    override fun onPermissionGranted() {
        settingViewModel.saveNotificationId()
    }

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
        val versionName = BuildConfig.VERSION_NAME
        binding.tvSettingAppVersionName.text = versionName
    }

    override fun shouldShowPermissionRationale(permission: String): Boolean =
        shouldShowRequestPermissionRationale(permission)

    private fun setupObservers() {
        settingViewModel.permissionCheckEvent.observe(viewLifecycleOwner) {
            notificationPermissionManager.requestNotificationPermission(
                requireContext(),
            )
        }
        settingViewModel.isAllowed.observe(viewLifecycleOwner) {
            binding.btnNoticeAllow.isChecked = it
        }
        settingViewModel.success.observe(viewLifecycleOwner) {
            requireActivity().showSnackBar(getString(R.string.setting_notice_enabled))
        }
        settingViewModel.error.observe(viewLifecycleOwner) { throwable ->
            showErrorSnackBar(throwable)
        }
        settingViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnNoticeAllow.isEnabled = !loading
        }

        homeViewModel.festivalUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FestivalUiState.Error -> {
                    showErrorSnackBar(state.throwable)
                    Timber.w(
                        state.throwable,
                        "${this::class.simpleName}: ${state.throwable.message}",
                    )
                }

                FestivalUiState.Loading -> {
                    binding.tvSettingCurrentUniversityNotice.text = ""
                }

                is FestivalUiState.Success -> {
                    binding.tvSettingCurrentUniversity.text = state.organization.universityName
                }
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
            // 기본적으로 클릭했을 때 checked되는 기능 무효화
            binding.btnNoticeAllow.isChecked = !binding.btnNoticeAllow.isChecked
            settingViewModel.notificationAllowClick()
        }
    }

    companion object {
        private const val POLICY_URL: String =
            "https://www.notion.so/244a540dc0b780638e56e31c4bdb3c9f"

        private const val CONTACT_US_URL =
            "https://forms.gle/XjqJFfQrTPgkZzGZ9"
    }
}
