package com.daedan.festabook.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ActivityMainBinding
import com.daedan.festabook.presentation.common.bottomNavigationViewAnimationCallback
import com.daedan.festabook.presentation.common.isGranted
import com.daedan.festabook.presentation.common.showToast
import com.daedan.festabook.presentation.common.toLocationPermissionDeniedTextOrNull
import com.daedan.festabook.presentation.home.HomeFragment
import com.daedan.festabook.presentation.news.NewsFragment
import com.daedan.festabook.presentation.placeList.PlaceListFragment
import com.daedan.festabook.presentation.schedule.ScheduleFragment
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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

    private val requestPermissionLauncher =
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

        requestNotificationPermission()
        setupHomeFragment(savedInstanceState)
        setUpBottomNavigation()
        onClickBottomNavigationBarItem()
        registerDeviceAndFcmToken()
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

    private fun registerDeviceAndFcmToken() {
        val app = application as FestaBookApp
        val prefsManager = app.appContainer.preferencesManager

        val uuid = prefsManager.getUuid().orEmpty()
        val fcmToken = prefsManager.getFcmToken().orEmpty()

        if (uuid.isNotBlank() && fcmToken.isNotBlank()) {
            viewModel.registerDevice(uuid, fcmToken)
        } else {
            Timber.w("UUID or FCM token is missing. Device registration skipped.")
            // 해당 로직 어떻게 구현할 지 생각하기
        }
    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // 이미 권한이 허용됨
                    Timber.d("Notification permission already granted")
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // 이전에 거부했지만 "다시 묻지 않음"을 선택하지 않은 경우
                    // 권한이 필요한 이유를 설명하는 UI(예: AlertDialog)를 표시
                    Timber.d("Show rationale for notification permission")
                    AlertDialog
                        .Builder(this)
                        .setTitle("알림 권한 필요")
                        .setMessage("새로운 소식 및 중요한 정보를 받기 위해 알림 권한이 필요합니다.")
                        .setPositiveButton("확인") { dialog, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            dialog.dismiss()
                        }.setNegativeButton("취소") { dialog, _ ->
                            showToast("알림 권한이 거부되었습니다.")
                            dialog.dismiss()
                        }.show()
                }

                else -> {
                    // 권한이 없으며, 이전에 "다시 묻지 않음"을 선택하지 않았거나 첫 요청인 경우
                    // 바로 권한 요청 다이얼로그 표시
                    Timber.d("Requesting notification permission for the first time or after 'don't ask again'")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Timber.d("Notification permission not required for API < 33")
        }
    }

    private fun setUpBottomNavigation() {
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

    private fun onClickBottomNavigationBarItem() {
        binding.bnvMenu.setOnItemSelectedListener { icon ->
            if (binding.bnvMenu.selectedItemId == icon.itemId) {
                return@setOnItemSelectedListener false
            }
            when (icon.itemId) {
                R.id.item_menu_home -> switchFragment(homeFragment, TAG_HOME_FRAGMENT)
                R.id.item_menu_schedule -> switchFragment(scheduleFragment, TAG_SCHEDULE_FRAGMENT)
                R.id.item_menu_map -> switchFragment(placeListFragment, TAG_PLACE_LIST_FRAGMENT)
                R.id.item_menu_news -> switchFragment(newFragment, TAG_NEW_FRAGMENT)
                R.id.item_menu_setting -> {}
            }
            true
        }
    }

    private fun switchFragment(
        fragment: Fragment,
        tag: String,
    ) {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(
            bottomNavigationViewAnimationCallback,
        )
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

        fun Fragment.newInstance(): Fragment =
            this.apply {
                arguments = Bundle()
            }
    }
}
