package com.daedan.festabook.presentation.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ActivityMainBinding
import com.daedan.festabook.presentation.NotificationPermissionManager
import com.daedan.festabook.presentation.NotificationPermissionRequester
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.isGranted
import com.daedan.festabook.presentation.common.showToast
import com.daedan.festabook.presentation.common.toLocationPermissionDeniedTextOrNull
import com.daedan.festabook.presentation.home.HomeFragment
import com.daedan.festabook.presentation.news.NewsFragment
import com.daedan.festabook.presentation.placeList.PlaceListFragment
import com.daedan.festabook.presentation.schedule.ScheduleFragment
import com.daedan.festabook.presentation.setting.SettingFragment
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class MainActivity :
    AppCompatActivity(),
    NotificationPermissionRequester {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    private val placeListFragment by lazy {
        PlaceListFragment().newInstance()
    }

    private val homeFragment by lazy {
        HomeFragment().newInstance()
    }

    private val scheduleFragment by lazy {
        ScheduleFragment().newInstance()
    }

    private val newFragment by lazy {
        NewsFragment().newInstance()
    }

    private val settingFragment by lazy {
        SettingFragment().newInstance()
    }

    private val notificationPermissionManager by lazy { NotificationPermissionManager(this) }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()

        registerDeviceAndFcmToken()
        notificationPermissionManager.requestNotificationPermission(this)
        setupHomeFragment(savedInstanceState)
        setUpBottomNavigation()
        onMenuItemClick()
        onMenuItemReClick()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        grantResults.forEachIndexed { index, result ->
            if (!result.isGranted()) {
                val text = permissions[index]
                showToast(
                    toLocationPermissionDeniedTextOrNull(text) ?: return@forEachIndexed,
                )
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun shouldShowPermissionRationale(permission: String): Boolean = shouldShowRequestPermissionRationale(permission)

    private fun registerDeviceAndFcmToken() {
        val app = application as FestaBookApp
        val prefsManager = app.appContainer.preferencesManager

        val uuid = prefsManager.getUuid().orEmpty()
        val fcmToken = prefsManager.getFcmToken()

        Timber.d("registerDeviceAndFcmToken() UUID: $uuid, FCM: $fcmToken")

        // UUID는 항상 있으므로, FCM 없으면 기다렸다가 호출
        if (uuid.isNotBlank() && fcmToken.isNullOrBlank()) {
            FirebaseMessaging
                .getInstance()
                .token
                .addOnSuccessListener { token ->
                    prefsManager.saveFcmToken(token)
                    Timber.d("🪄 받은 FCM 토큰으로 디바이스 등록: $token")
                    viewModel.registerDevice(uuid, token)
                }.addOnFailureListener {
                    Timber.w(it, "❌ FCM 토큰 받기 실패")
                }
        } else if (fcmToken != null) {
            if (uuid.isNotBlank() && fcmToken.isNotBlank()) {
                Timber.d("✅ 기존 값으로 디바이스 등록 실행")
                viewModel.registerDevice(uuid, fcmToken)
            } else {
                Timber.w("❌ UUID 생성 전 or FCM 토큰 없음")
            }
        }
    }

    private fun setupBinding() {
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUpBottomNavigation() {
        binding.fabMap.post {
            binding.fabMap.translationY = FLOATING_ACTION_BUTTON_INITIAL_TRANSLATION_Y
            binding.fcvFragmentContainer.updatePadding(
                bottom = binding.babMenu.height + binding.babMenu.marginBottom,
            )
            binding.bnvMenu.x /= 2
        }
        binding.babMenu.setOnApplyWindowInsetsListener(null)
        binding.babMenu.setPadding(0, 0, 0, 0)
        binding.bnvMenu.setOnApplyWindowInsetsListener(null)
        binding.bnvMenu.setPadding(0, 0, 0, 0)
    }

    private fun setupHomeFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fcv_fragment_container, homeFragment, TAG_HOME_FRAGMENT)
            }
        }
    }

    private fun onMenuItemClick() {
        binding.bnvMenu.setOnItemSelectedListener { icon ->
            when (icon.itemId) {
                R.id.item_menu_home -> switchFragment(homeFragment, TAG_HOME_FRAGMENT)
                R.id.item_menu_schedule -> switchFragment(scheduleFragment, TAG_SCHEDULE_FRAGMENT)
                R.id.item_menu_news -> switchFragment(newFragment, TAG_NEW_FRAGMENT)
                R.id.item_menu_setting -> switchFragment(settingFragment, TAG_SETTING_FRAGMENT)
            }
            true
        }
        binding.fabMap.setOnClickListener {
            binding.bnvMenu.selectedItemId = R.id.item_menu_map
            val fragment = supportFragmentManager.findFragmentByTag(TAG_PLACE_LIST_FRAGMENT)
            if (fragment is OnMenuItemReClickListener) fragment.onMenuItemReClick()
            switchFragment(placeListFragment, TAG_PLACE_LIST_FRAGMENT)
        }
    }

    private fun onMenuItemReClick() {
        binding.bnvMenu.setOnItemReselectedListener { icon ->
            when (icon.itemId) {
                R.id.item_menu_home -> Unit
                R.id.item_menu_schedule -> {
                    val fragment = supportFragmentManager.findFragmentByTag(TAG_SCHEDULE_FRAGMENT)
                    if (fragment is OnMenuItemReClickListener) fragment.onMenuItemReClick()
                }

                R.id.item_menu_news -> Unit
                R.id.item_menu_setting -> Unit
            }
        }
    }

    private fun switchFragment(
        fragment: Fragment,
        tag: String,
    ) {
        supportFragmentManager.commit {
            supportFragmentManager.fragments.forEach { fragment -> hide(fragment) }

            val existing = supportFragmentManager.findFragmentByTag(tag)
            if (existing != null) {
                show(existing)
            } else {
                add(R.id.fcv_fragment_container, fragment, tag)
            }
            setReorderingAllowed(true)
        }
    }

    companion object {
        private const val TAG_HOME_FRAGMENT = "homeFragment"
        private const val TAG_SCHEDULE_FRAGMENT = "scheduleFragment"
        private const val TAG_PLACE_LIST_FRAGMENT = "placeListFragment"
        private const val TAG_NEW_FRAGMENT = "newFragment"
        private const val TAG_SETTING_FRAGMENT = "settingFragment"
        private const val FLOATING_ACTION_BUTTON_INITIAL_TRANSLATION_Y = 0f

        fun Fragment.newInstance(): Fragment =
            this.apply {
                arguments = Bundle()
            }
    }
}
