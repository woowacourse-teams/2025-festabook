package com.daedan.festabook.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.daedan.festabook.R
import com.daedan.festabook.presentation.explore.ExploreActivity
import com.daedan.festabook.presentation.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels { SplashViewModel.FACTORY }
    private var launcher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                setupObserver()
            } else {
                exitDialog().show()
            }
        }
    private val appVersionManager by lazy {
        AppVersionManager(
            AppUpdateManagerFactory.create(this),
            launcher,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isValidationComplete.value != true
        }
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkIsAppUpdateAvailable {
            setupObserver()
        }
    }

    private fun checkIsAppUpdateAvailable(onSuccess: () -> Unit) {
        if (!isNetworkConnected()) {
            exitDialog().show()
            return
        }

        lifecycleScope.launch {
            appVersionManager
                .getIsAppUpdateAvailable()
                .onSuccess { isUpdateAvailable ->
                    if (isUpdateAvailable) {
                        updateDialog {
                            appVersionManager.updateApp()
                        }.show()
                    } else {
                        onSuccess()
                    }
                }.onFailure {
                    exitDialog().show()
                }
        }
    }

    private fun setupObserver() {
        viewModel.navigationState.observe(this) { state ->
            when (state) {
                is NavigationState.NavigateToExplore -> {
                    // ExploreActivity로 이동
                    val intent = Intent(this@SplashActivity, ExploreActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is NavigationState.NavigateToMain -> {
                    // MainActivity로 이동
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
