package com.daedan.festabook.presentation.explore

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.daedan.festabook.R
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

    override fun onUniversityClick(university: University) {
        binding.etSearchText.setText(university.universityName)
        binding.etSearchText.setSelection(university.universityName.length)

        viewModel.onUniversitySelected(university)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setContentView(binding.root)

        setupBinding()
        setupRecyclerView()
        setupObservers()

        viewModel.navigateToMainScreen()
    }

    private fun setupBinding() {
        binding.etSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilSearchInputLayout.boxStrokeColor = getColor(R.color.blue400)
            } else {
                binding.tilSearchInputLayout.boxStrokeColor = getColor(R.color.gray400)
            }
        }

        binding.etSearchText.doOnTextChanged { text, _, _, _ ->
            viewModel.onTextInputChanged(text?.toString().orEmpty())
            binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM

            if (text.isNullOrEmpty()) {
                // 검색 아이콘
                binding.tilSearchInputLayout.setEndIconDrawable(R.drawable.ic_search)
                binding.tilSearchInputLayout.setEndIconOnClickListener {
                    handleSearchAction()
                }
                binding.tilSearchInputLayout.endIconContentDescription = "검색"
            } else {
                // X 아이콘
                binding.tilSearchInputLayout.setEndIconDrawable(R.drawable.ic_close)
                binding.tilSearchInputLayout.setEndIconOnClickListener {
                    binding.etSearchText.text?.clear()
                }
                binding.tilSearchInputLayout.endIconContentDescription = "입력 내용 지우기"
            }
        }

        // 키보드 엔터(검색) 리스너
        binding.etSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearchAction()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun handleSearchAction() {
        hideKeyboard()
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
                    searchResultAdapter.submitList(emptyList())
                }

                is SearchUiState.Loading -> Unit
                is SearchUiState.Success -> {
                    // 검색 결과가 없을 때
                    if (state.universitiesFound.isEmpty()) {
                        binding.tilSearchInputLayout.isErrorEnabled = true
                        binding.tilSearchInputLayout.error =
                            getString(R.string.explore_no_search_result_text)
                        binding.tilSearchInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    } else {
                        // 검색 결과가 있을 때
                        binding.tilSearchInputLayout.isErrorEnabled = false
                        searchResultAdapter.submitList(state.universitiesFound)
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
                navigateToMainActivity(university.festivalId)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchText.windowToken, 0)
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
