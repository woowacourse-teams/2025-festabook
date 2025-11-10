package com.daedan.festabook.presentation.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.lifecycle.ViewModelProvider
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ActivityMainBinding
import com.daedan.festabook.di.appGraph
import com.daedan.festabook.presentation.NotificationPermissionManager
import com.daedan.festabook.presentation.NotificationPermissionRequester
import com.daedan.festabook.presentation.common.OnMenuItemReClickListener
import com.daedan.festabook.presentation.common.isGranted
import com.daedan.festabook.presentation.common.showNotificationDeniedSnackbar
import com.daedan.festabook.presentation.common.showSnackBar
import com.daedan.festabook.presentation.common.showToast
import com.daedan.festabook.presentation.home.HomeFragment
import com.daedan.festabook.presentation.home.HomeViewModel
import com.daedan.festabook.presentation.news.NewsFragment
import com.daedan.festabook.presentation.placeMap.PlaceMapFragment
import com.daedan.festabook.presentation.schedule.ScheduleFragment
import com.daedan.festabook.presentation.setting.SettingFragment
import com.daedan.festabook.presentation.setting.SettingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.zacsweers.metro.Inject
import timber.log.Timber

class MainActivity :
    AppCompatActivity(),
    NotificationPermissionRequester {

    @Inject
    override lateinit var defaultViewModelProviderFactory: ViewModelProvider.Factory

    @Inject
    private lateinit var fragmentFactory: FragmentFactory

    @Inject
    private lateinit var notificationPermissionManagerFactory: NotificationPermissionManager.Factory
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    private val notificationPermissionManager by lazy {
        notificationPermissionManagerFactory.create(
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
                showNotificationDeniedSnackbar(window.decorView.rootView, this)
                onPermissionDenied()
            }
        }

    override fun onPermissionGranted() {
        settingViewModel.saveNotificationId()
    }

    override fun onPermissionDenied() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        appGraph.inject(this)
        setupFragmentFactory()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        mainViewModel.registerDeviceAndFcmToken()
        setupHomeFragment(savedInstanceState)
        setUpBottomNavigation()
        setupObservers()
        onMenuItemClick()
        onMenuItemReClick()
        onBackPress()
        handleNavigation(intent)
    }

    private fun setupFragmentFactory() {
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        grantResults.forEachIndexed { index, result ->
            val text = permissions[index]
            when(text) {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    if (!result.isGranted()) {
                        showNotificationDeniedSnackbar(
                            binding.root,
                            this,
                            getString(R.string.map_request_location_permission_message)
                        )
                    }
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun shouldShowPermissionRationale(permission: String): Boolean =
        shouldShowRequestPermissionRationale(permission)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNavigation(intent)
    }

    private fun handleNavigation(intent: Intent) {
        val canNavigateToNewsScreen =
            intent.getBooleanExtra(KEY_CAN_NAVIGATE_TO_NEWS, false)
        val noticeIdToExpand = intent.getLongExtra(KEY_NOTICE_ID_TO_EXPAND, INITIALIZED_ID)
        if (noticeIdToExpand != INITIALIZED_ID) mainViewModel.expandNoticeItem(noticeIdToExpand)

        if (canNavigateToNewsScreen) {
            binding.bnvMenu.selectedItemId = R.id.item_menu_news
        }
    }

    private fun setupObservers() {
        mainViewModel.backPressEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { isDoublePress ->
                if (isDoublePress) finish() else showToast(getString(R.string.back_press_exit_message))
            }
        }
        homeViewModel.navigateToScheduleEvent.observe(this) {
            binding.bnvMenu.selectedItemId = R.id.item_menu_schedule
        }

        mainViewModel.isFirstVisit.observe(this) { isFirstVisit ->
            if (isFirstVisit) {
                showAlarmDialog()
            }
        }
        settingViewModel.success.observe(this) {
            showSnackBar(getString(R.string.setting_notice_enabled))
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
            binding.fcvFragmentContainer.updatePadding(
                bottom = binding.babMenu.height + binding.babMenu.marginBottom,
            )
        }
        binding.babMenu.setOnApplyWindowInsetsListener(null)
        binding.babMenu.setPadding(0, 0, 0, 0)
        binding.bnvMenu.setOnApplyWindowInsetsListener(null)
        binding.bnvMenu.setPadding(0, 0, 0, 0)
    }

    private fun setupHomeFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                add<HomeFragment>(R.id.fcv_fragment_container)
            }
        }
    }

    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainViewModel.onBackPressed()
                }
            },
        )
    }

    private fun onMenuItemClick() {
        binding.bnvMenu.setOnItemSelectedListener { icon ->
            when (icon.itemId) {
                R.id.item_menu_home -> switchFragment(HomeFragment::class.java, TAG_HOME_FRAGMENT)
                R.id.item_menu_schedule ->
                    switchFragment(
                        ScheduleFragment::class.java,
                        TAG_SCHEDULE_FRAGMENT,
                    )

                R.id.item_menu_news -> switchFragment(NewsFragment::class.java, TAG_NEWS_FRAGMENT)
                R.id.item_menu_setting ->
                    switchFragment(
                        SettingFragment::class.java,
                        TAG_SETTING_FRAGMENT,
                    )
            }
            true
        }
        binding.fabMap.setOnClickListener {
            binding.bnvMenu.selectedItemId = R.id.item_menu_map
            val fragment = supportFragmentManager.findFragmentByTag(TAG_PLACE_MAP_FRAGMENT)
            if (fragment is OnMenuItemReClickListener && !fragment.isHidden) fragment.onMenuItemReClick()
            switchFragment(PlaceMapFragment::class.java, TAG_PLACE_MAP_FRAGMENT)
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
        fragment: Class<out Fragment>,
        tag: String,
    ) {
        supportFragmentManager.commit {
            supportFragmentManager.fragments.forEach { fragment -> hide(fragment) }

            val existing = supportFragmentManager.findFragmentByTag(tag)
            if (existing != null) {
                show(existing)
            } else {
                add(R.id.fcv_fragment_container, fragment, null, tag)
            }
            setReorderingAllowed(true)
        }
    }

    private fun showAlarmDialog() {
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.MainAlarmDialogTheme)
                .setView(R.layout.view_main_alert_dialog)
                .setPositiveButton(R.string.main_alarm_dialog_confirm_button) { _, _ ->
                    notificationPermissionManager.requestNotificationPermission(this)
                }.setNegativeButton(R.string.main_alarm_dialog_cancel_button) { dialog, _ ->
                    dialog.dismiss()
                }.create()
        dialog.show()
    }

    companion object {
        const val KEY_NOTICE_ID_TO_EXPAND = "noticeIdToExpand"
        const val KEY_CAN_NAVIGATE_TO_NEWS = "canNavigateToNews"
        private const val TAG_HOME_FRAGMENT = "homeFragment"
        private const val TAG_SCHEDULE_FRAGMENT = "scheduleFragment"
        private const val TAG_PLACE_MAP_FRAGMENT = "placeMapFragment"
        private const val TAG_NEWS_FRAGMENT = "newsFragment"
        private const val TAG_SETTING_FRAGMENT = "settingFragment"
        private const val INITIALIZED_ID = -1L

        fun newIntent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
    }
}
