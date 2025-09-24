package com.daedan.festabook.presentation.error

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ActivityErrorBinding
import com.daedan.festabook.logging.logger
import com.daedan.festabook.presentation.common.getSerializableCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics

class ErrorActivity : AppCompatActivity(R.layout.activity_error) {
    private val binding: ActivityErrorBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_error)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        intent.getSerializableCompat<Throwable>(KEY_ERROR)?.let {
            FirebaseCrashlytics.getInstance().recordException(it)
        }

        binding.btnUncaughtErrorConfirm.setOnClickListener {
            finishAffinity()
        }
    }

    companion object {
        private const val KEY_ERROR = "error"
        fun newIntent(context: Context, error: Throwable): Intent =
            Intent(context, ErrorActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(KEY_ERROR, error)
            }
    }
}