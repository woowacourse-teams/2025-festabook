package com.daedan.festabook.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.daedan.festabook.presentation.placeList.placeMap.PlaceMapFragment
import com.daedan.festabook.presentation.schedule.ScheduleFragment
import com.daedan.festabook.presentation.setting.SettingFragment
import timber.log.Timber

class MainActivity :
    AppCompatActivity(),
    NotificationPermissionRequester {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    private val notificationPermissionManager by lazy {
        NotificationPermissionManager(this)
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

    override fun onPermissionDenied() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        setupObservers()

        viewModel.registerDeviceAndFcmToken()
        notificationPermissionManager.requestNotificationPermission(this)
        setupHomeFragment(savedInstanceState)
        setUpBottomNavigation()
        setupObservers()
        onMenuItemClick()
        onMenuItemReClick()
        onBackPress()
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

    private fun setupObservers() {
        viewModel.backPressEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { isDoublePress ->
                if (isDoublePress) finish() else showToast(getString(R.string.back_press_exit_message))
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

    private fun setupHomeFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fcv_fragment_container, HomeFragment().newInstance(), TAG_HOME_FRAGMENT)
            }
        }
    }

  private fun setupObservers() {
        binding.lifecycleOwner = this

        viewModel.selectedItemId.observe(this) { itemId ->
            val tag =
                when (itemId) {
                    R.id.item_menu_home -> TAG_HOME_FRAGMENT
                    R.id.item_menu_schedule -> TAG_SCHEDULE_FRAGMENT
                    R.id.item_menu_news -> TAG_NEW_FRAGMENT
                    R.id.item_menu_setting -> TAG_SETTING_FRAGMENT
                    R.id.item_menu_map -> TAG_PLACE_MAP_FRAGMENT
                    else -> ""
                }

            if (tag.isNotEmpty()) {
                switchFragmentByTag(tag)
            }

            binding.bnvMenu.selectedItemId = itemId
        }
    }
  
    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackPressed()
                }
            },
        )
    }

    private fun onMenuItemClick() {
        binding.bnvMenu.setOnItemSelectedListener { item ->
            viewModel.selectItem(item.itemId)
            true
        }
        binding.fabMap.setOnClickListener {
            viewModel.selectItem(R.id.item_menu_map)
        }
    }

    private fun onMenuItemReClick() {
        binding.bnvMenu.setOnItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
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

    private fun switchFragmentByTag(tag: String) {
        // 기존 프래그먼트 찾기
        val fragment =
            supportFragmentManager.findFragmentByTag(tag)
                ?: getFragmentByTag(tag) // 없으면 새로 생성

        supportFragmentManager.commit {
            // 다른 프래그먼트는 숨기기
            supportFragmentManager.fragments.forEach { currentFragment ->
                if (currentFragment != fragment) hide(currentFragment)
            }

            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.fcv_fragment_container, fragment, tag)
            }
        }
    }

    private fun getFragmentByTag(tag: String): Fragment {
        val fragment =
            when (tag) {
                TAG_HOME_FRAGMENT -> HomeFragment().newInstance()
                TAG_SCHEDULE_FRAGMENT -> ScheduleFragment().newInstance()
                TAG_PLACE_MAP_FRAGMENT -> PlaceMapFragment().newInstance()
                TAG_NEW_FRAGMENT -> NewsFragment().newInstance()
                TAG_SETTING_FRAGMENT -> SettingFragment().newInstance()
                else -> throw IllegalArgumentException("Invalid fragment tag: $tag")
            }
        return fragment
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

    companion object {
        private const val TAG_HOME_FRAGMENT = "homeFragment"
        private const val TAG_SCHEDULE_FRAGMENT = "scheduleFragment"
        private const val TAG_PLACE_MAP_FRAGMENT = "placeMapFragment"
        private const val TAG_NEW_FRAGMENT = "newFragment"
        private const val TAG_SETTING_FRAGMENT = "settingFragment"
        private const val FLOATING_ACTION_BUTTON_INITIAL_TRANSLATION_Y = 0f

        fun Fragment.newInstance(): Fragment =
            this.apply {
                arguments = Bundle()
            }
    }
}
