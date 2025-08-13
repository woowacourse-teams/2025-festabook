package com.daedan.festabook.presentation.explore

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.daedan.festabook.R
import com.daedan.festabook.data.datasource.local.AppPreferencesManager
import com.daedan.festabook.databinding.ActivityExploreBinding
import com.daedan.festabook.domain.model.University
import com.daedan.festabook.presentation.main.MainActivity
import com.google.android.material.textfield.TextInputLayout

class ExploreActivity : AppCompatActivity(R.layout.activity_explore) {
    private val binding by lazy { ActivityExploreBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ExploreViewModel> { ExploreViewModel.Factory }

    private lateinit var appPreferencesManager: AppPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        appPreferencesManager = AppPreferencesManager(this)

        setupBinding()
        setupObservers()
    }

    private fun setupBinding() {
        binding.tilSearchInputLayout.setEndIconOnClickListener {
            val query = binding.etSearchText.text.toString()
            val currentState = viewModel.searchState.value

            when (currentState) {
                is SearchUiState.Success -> viewModel.onNavigateIconClicked()
                else -> viewModel.search(query)
            }
        }

        binding.etSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilSearchInputLayout.boxStrokeColor = getColor(R.color.blue400)
            } else {
                binding.tilSearchInputLayout.boxStrokeColor = getColor(R.color.gray400)
            }
        }
    }

    private fun setupObservers() {
        viewModel.searchState.observe(this) { state ->
            when (state) {
                is SearchUiState.Idle -> {
                    binding.tilSearchInputLayout.isErrorEnabled = false
                }

                is SearchUiState.Loading -> Unit
                is SearchUiState.Success -> {
                    // 검색 결과가 없을 때
                    if (state.value == null) {
                        binding.tilSearchInputLayout.isErrorEnabled = true
                        binding.tilSearchInputLayout.error = "검색 결과가 없습니다."
                        binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    } else {
                        // 검색 결과가 있을 때
                        binding.tilSearchInputLayout.isErrorEnabled = false
                        binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                        binding.tilSearchInputLayout.setEndIconDrawable(R.drawable.ic_arrow_right)
                    }
                }

                is SearchUiState.Error -> {
                    binding.tilSearchInputLayout.isErrorEnabled = true
                    binding.tilSearchInputLayout.error = "오류가 발생했습니다: ${state.throwable.message}"
                    binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
        }

        viewModel.navigateToMain.observe(this) { university ->
            university?.let {
                saveFestivalIdToLocal(it)
                navigateToMainActivity()
            }
        }
    }

    private fun saveFestivalIdToLocal(it: University) {
        appPreferencesManager.saveFestivalId(it.festivalId)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
