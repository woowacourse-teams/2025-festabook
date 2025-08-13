package com.daedan.festabook.presentation.explore

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.daedan.festabook.R
import com.daedan.festabook.data.datasource.local.AppPreferencesManager
import com.daedan.festabook.databinding.ActivityExploreBinding
import com.daedan.festabook.domain.model.University
import com.daedan.festabook.presentation.main.MainActivity
import com.google.android.material.textfield.TextInputLayout
import timber.log.Timber

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
        setOnSearchIconClickListener()

        binding.etSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilSearchInputLayout.boxStrokeColor = getColor(R.color.blue400)
            } else {
                binding.tilSearchInputLayout.boxStrokeColor = getColor(R.color.gray400)
            }
        }

        binding.etSearchText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    viewModel.onTextInputChanged()
                }

                override fun afterTextChanged(s: Editable?) {}
            },
        )
    }

    private fun setOnSearchIconClickListener() {
        binding.tilSearchInputLayout.setEndIconOnClickListener {
            val query = binding.etSearchText.text.toString()
            val currentState = viewModel.searchState.value

            Timber.d("current state: $currentState")

            when (currentState) {
                is SearchUiState.Idle -> {
                    viewModel.search(query)
                }

                is SearchUiState.Loading -> {}
                is SearchUiState.Success -> {
                    viewModel.onNavigateIconClicked()
                }

                is SearchUiState.Error -> {
                    viewModel.search(query)
                }

                null -> {
                    viewModel.search(query)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.searchState.observe(this) { state ->
            Timber.d("current state: $state")
            when (state) {
                is SearchUiState.Idle -> {
                    binding.tilSearchInputLayout.isErrorEnabled = false
                    binding.tilSearchInputLayout.error = null
                    binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    binding.tilSearchInputLayout.setEndIconDrawable(R.drawable.ic_search)
                    setOnSearchIconClickListener()
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
                        setOnSearchIconClickListener()
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
