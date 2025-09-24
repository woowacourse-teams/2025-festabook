package com.daedan.festabook.presentation.error

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daedan.festabook.R
import com.daedan.festabook.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity(R.layout.activity_error) {
    private val binding: ActivityErrorBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_error)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding.btnUncaughtErrorConfirm.setOnClickListener {
            finishAffinity()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, ErrorActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
    }
}