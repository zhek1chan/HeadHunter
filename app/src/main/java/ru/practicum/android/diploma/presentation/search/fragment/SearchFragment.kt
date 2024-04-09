package ru.practicum.android.diploma.presentation.search.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.presentation.search.SearchState
import ru.practicum.android.diploma.presentation.search.adapter.PagesAdapter
import ru.practicum.android.diploma.presentation.search.adapter.SearchLoadStateAdapter
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()
    private var pAdapter: PagesAdapter? = null
    private var searchText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vacancies = binding.searchRecyclerView
        pAdapter = pAdapter ?: PagesAdapter(::navToVacancyDetails).apply {
            this.addLoadStateListener(viewModel::listenerOfStates)
        }

        vacancies.adapter = pAdapter?.withLoadStateFooter(
            footer = SearchLoadStateAdapter(),
        )

        vacancies.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            viewModel.stateVacancyData.collectLatest {
                pAdapter?.submitData(it)
            }
        }

        lifecycleScope.launch {
            viewModel.searchState.collect {
                when (it) {
                    SearchState.FailedToGetList -> {
                        binding.imageBinoculars.gone()
                        binding.searchRecyclerView.gone()
                        binding.placeholderNoConnection.gone()
                        binding.placeholderError.visible()
                        binding.progressBar.gone()
                        binding.messageText.visible()
                        binding.messageText.text = getString(R.string.there_are_no_such_vacs)
                    }

                    SearchState.Loaded -> {
                        binding.imageBinoculars.gone()
                        binding.searchRecyclerView.visible()
                        binding.placeholderError.gone()
                        binding.placeholderNoConnection.gone()
                        binding.progressBar.gone()
                        binding.messageText.visible()
                        binding.messageText.text = getString(R.string.found_x_vacancies, SearchState.Loaded.counter)
                        binding.placeholderError.gone()
                    }

                    SearchState.Loading -> {
                        try {
                            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                        } catch (_: Throwable) {
                        }
                        binding.imageBinoculars.gone()
                        binding.searchRecyclerView.gone()
                        binding.placeholderError.gone()
                        binding.placeholderNoConnection.gone()
                        binding.progressBar.visible()
                        binding.messageText.gone()
                    }

                    SearchState.NoInternet -> {
                        binding.imageBinoculars.gone()
                        binding.searchRecyclerView.gone()
                        binding.placeholderNoConnection.visible()
                        binding.placeholderError.gone()
                        binding.progressBar.gone()
                        binding.messageText.gone()
                    }

                    SearchState.ServerError -> {
                        binding.imageBinoculars.gone()
                        binding.searchRecyclerView.gone()
                        binding.placeholderNoConnection.gone()
                        binding.placeholderError.visible()
                        binding.progressBar.gone()
                        binding.messageText.gone()
                    }

                    SearchState.Search -> {
                        binding.imageBinoculars.gone()
                        binding.searchRecyclerView.gone()
                        binding.placeholderError.gone()
                        binding.placeholderNoConnection.gone()
                        binding.progressBar.gone()
                        binding.messageText.gone()
                    }

                    SearchState.Start -> {
                        binding.imageBinoculars.visible()
                        binding.searchRecyclerView.gone()
                        binding.placeholderError.gone()
                        binding.placeholderNoConnection.gone()
                        binding.progressBar.gone()
                        binding.messageText.gone()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.stateBottomFilters.collect {
                if (it) {
                    binding.filtersOn.gone()
                    binding.filtersOff.visible()
                } else {
                    binding.filtersOn.visible()
                    binding.filtersOff.gone()
                }
            }
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
        }

        binding.searchEditText.addTextChangedListener {
            viewModel.search(it?.toString() ?: "")
            changeClearButton()
        }

        binding.searchEditText.addTextChangedListener(afterTextChanged = ::searchExample)

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
        checkFiltersButton()
    }

    private fun navToVacancyDetails(vacancy: Vacancy) {
        val navController = findNavController()
        val bundle = Bundle()
        bundle.putParcelable("vacancyId", vacancy)
        navController.navigate(R.id.vacancyFragment, bundle)
    }

    private fun searchExample(it: Editable?) {
        viewModel.search(it?.toString() ?: "")
    }

    private fun changeClearButton() {
        if (searchText.isNullOrEmpty()) {
            binding.clearButton.setBackgroundResource(R.drawable.ic_search)
        } else {
            binding.clearButton.setBackgroundResource(R.drawable.close_24px)
            binding.clearButton.setOnClickListener {
                binding.searchEditText.setText("")
                searchText = null
                Log.d("SearchFragment", "Tap on X")
            }
        }
    }

    private fun checkFiltersButton() {
        binding.filtersOff.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_filterFragment)
        }
        binding.filtersOn.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_filterFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkFiltersChanges()
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}
