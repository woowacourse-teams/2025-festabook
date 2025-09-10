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
import com.daedan.festabook.FestaBookApp
import com.daedan.festabook.R
import com.daedan.festabook.presentation.common.showErrorSnackBar
import com.daedan.festabook.presentation.explore.ExploreActivity
import com.daedan.festabook.presentation.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels { SplashViewModel.FACTORY }
    private val launcher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) {
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
        setupObserver()
        checkIsAppUpdateAvailable()
    }

    private fun checkIsAppUpdateAvailable() {
        lifecycleScope.launch {
            appVersionManager
                .getIsAppUpdateAvailable()
                .onSuccess { isUpdateAvailable ->
                }.onFailure { e ->
                    showErrorSnackBar(e)
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
//                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
                    updateDialog {
                        appVersionManager.updateApp()
                    }.show()

//                    lifecycleScope.launch {
//                        appVersionManager
//                            .getIsAppUpdateAvailable()
//                            .onSuccess { isUpdateAvailable ->
//                                if (isUpdateAvailable) {
//                                    updateDialog { _, _ ->
//                                        appVersionManager.updateApp()
//                                    }.show()
//                                }
//                            }
//                    }
                }
            }
        }
    }

    private fun updateDialog(listener: () -> Unit): AlertDialog {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.view_app_update_alert_dialog, null)
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.MainAlarmDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create()

        dialogView.findViewById<Button>(R.id.btn_dialog_confirm)?.setOnClickListener {
            listener()
            dialog.dismiss()
        }
        return dialog
    }
}
