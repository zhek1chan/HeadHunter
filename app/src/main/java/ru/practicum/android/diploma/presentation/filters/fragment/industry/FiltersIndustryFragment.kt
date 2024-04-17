package ru.practicum.android.diploma.presentation.filters.fragment.industry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterChooseIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview.IndustriesAdapter
import ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview.IndustryClick
import ru.practicum.android.diploma.presentation.filters.viewmodel.industry.FiltersIndustryViewModel

class FiltersIndustryFragment : Fragment() {
    private var _binding: FragmentFilterChooseIndustryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersIndustryViewModel by viewModel()

    private val industriesAdapter = IndustriesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFilterChooseIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        industriesAdapter.setIndustryClickListener(object : IndustryClick {
            override fun click(industry: Industry) {
                viewModel.clickIndustry(industry)
                industriesAdapter.notifyDataSetChanged()
            }
        })

        binding.industryList.layoutManager = LinearLayoutManager(requireContext())
        binding.industryList.adapter = industriesAdapter

        binding.arrowBackButton.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.searchIndustry.doOnTextChanged { text, _, _, _ ->
            viewModel.filterIndustries(text.toString())
            if (text.isNullOrBlank()) {
                binding.clearButton.visibility = View.GONE
                binding.searchDrawable.visibility = View.VISIBLE
            } else {
                binding.clearButton.visibility = View.VISIBLE
                binding.searchDrawable.visibility = View.GONE
                binding.clearButton.setOnClickListener {
                    binding.searchIndustry.text?.clear()
                }
            }
        }

        viewModel.getIndustriesState().observe(viewLifecycleOwner) { requestState ->
            when (requestState) {
                is RequestIndustriesState.Loading -> showLoading()
                is RequestIndustriesState.Error -> showError()
                is RequestIndustriesState.Empty -> showEmpty()
                is RequestIndustriesState.Success -> {
                    industriesAdapter.industryList = requestState.data
                    industriesAdapter.notifyDataSetChanged()
                    showContent()
                }
            }
        }

        viewModel.getChosenIndustry().observe(viewLifecycleOwner) { chosenIndustry ->
            if (chosenIndustry != null) {
                binding.choseBtn.visibility = View.VISIBLE
            } else {
                binding.choseBtn.visibility = View.GONE
            }
        }

        binding.choseBtn.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                INDUSTRY_KEY,
                bundleOf(INDUSTRY to viewModel.getChosenIndustry().value)
            )
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent() {
        binding.choseBtn.visibility = if (viewModel.getChosenIndustry().value != null) View.VISIBLE else View.GONE
        binding.industryList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.errorFailedGet.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.choseBtn.visibility = View.GONE
        binding.industryList.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        binding.errorFailedGetImage.setImageResource(R.drawable.ph_nothing_found)
        binding.errorFailedGetText.setText(R.string.industry_not_found)
        binding.errorFailedGet.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.choseBtn.visibility = View.GONE
        binding.industryList.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.errorFailedGetImage.setImageResource(R.drawable.error_region_list)
        binding.errorFailedGetText.setText(R.string.failed_to_get_list)
        binding.errorFailedGet.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.choseBtn.visibility = View.GONE
        binding.industryList.visibility = View.GONE
        binding.errorFailedGet.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    companion object {
        const val INDUSTRY_KEY = "INDUSTRY_KEY"
        const val INDUSTRY = "INDUSTRY"
    }

}
