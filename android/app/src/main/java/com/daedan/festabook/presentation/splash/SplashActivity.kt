package com.daedan.festabook.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.daedan.festabook.R
import com.daedan.festabook.presentation.explore.ExploreActivity
import com.daedan.festabook.presentation.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels { SplashViewModel.FACTORY }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isValidationComplete.value != true
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupObserver()
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
