package com.daedan.festabook.presentation.explore

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.daedan.festabook.R
import com.daedan.festabook.data.datasource.local.AppPreferencesManager
import com.daedan.festabook.databinding.ActivityExploreBinding
import com.daedan.festabook.domain.model.University
import com.daedan.festabook.presentation.explore.adapter.OnUniversityClickListener
import com.daedan.festabook.presentation.explore.adapter.SearchResultAdapter
import com.daedan.festabook.presentation.main.MainActivity
import com.google.android.material.textfield.TextInputLayout

class ExploreActivity :
    AppCompatActivity(),
    OnUniversityClickListener {
    private val binding by lazy { ActivityExploreBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ExploreViewModel> { ExploreViewModel.Factory }
    private val searchResultAdapter by lazy { SearchResultAdapter(this) }

    private lateinit var appPreferencesManager: AppPreferencesManager

    override fun onUniversityClick(university: University) {
        binding.etSearchText.setText(university.universityName)
        binding.etSearchText.setSelection(university.universityName.length)

        viewModel.onUniversitySelected(university)
        binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.tilSearchInputLayout.setEndIconDrawable(R.drawable.ic_arrow_right)
        setOnSearchIconClickListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        appPreferencesManager = AppPreferencesManager(this)

        setupBinding()
        setupRecyclerView()
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

        binding.etSearchText.doOnTextChanged { _, _, _, _ ->
            viewModel.onTextInputChanged()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSearchResults.adapter = searchResultAdapter
    }

    private fun setupObservers() {
        viewModel.searchState.observe(this) { state ->
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
                    if (state.value.isEmpty()) {
                        binding.tilSearchInputLayout.isErrorEnabled = true
                        binding.tilSearchInputLayout.error =
                            getString(R.string.explore_no_search_result_text)
                        binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    } else {
                        // 검색 결과가 있을 때
                        binding.tilSearchInputLayout.isErrorEnabled = false
                        searchResultAdapter.submitList(state.value)
                    }
                }

                is SearchUiState.Error -> {
                    binding.tilSearchInputLayout.isErrorEnabled = true
                    binding.tilSearchInputLayout.error =
                        getString(R.string.explore_error_text, state.throwable.message)
                    binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
        }

        viewModel.navigateToMain.observe(this) { university ->
            university?.let {
                saveFestivalIdToLocal(it)
                navigateToMainActivity(university.festivalId)
            }
        }
    }

    private fun setOnSearchIconClickListener() {
        binding.tilSearchInputLayout.setEndIconOnClickListener {
            val query = binding.etSearchText.text.toString()
            val currentState = viewModel.searchState.value

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

    private fun saveFestivalIdToLocal(it: University) {
        appPreferencesManager.saveFestivalId(it.festivalId)
    }

    private fun navigateToMainActivity(festivalId: Long) {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                putExtra("festival_id", festivalId)
            }
        startActivity(intent)
        finish()
    }
}
