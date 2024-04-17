package ru.practicum.android.diploma.presentation.filters.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.SubIndustry
import ru.practicum.android.diploma.presentation.filters.fragment.country.FiltersCountryFragment
import ru.practicum.android.diploma.presentation.filters.fragment.industry.FiltersIndustryFragment
import ru.practicum.android.diploma.presentation.filters.fragment.placeofwork.FiltersPlaceOfWorkFragment
import ru.practicum.android.diploma.presentation.filters.fragment.region.FiltersRegionFragment
import ru.practicum.android.diploma.presentation.filters.viewmodel.main.FiltersViewModel
import ru.practicum.android.diploma.presentation.search.fragment.gone
import ru.practicum.android.diploma.presentation.search.fragment.visible

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FiltersViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        setTextListeners()

        binding.industryLayout.setOnClickListener {
            findNavController().navigate(R.id.chooseIndustryFragment)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.filterState.collect {
                setFilters(it.filters)
                saveButtonVisabilityPick(it.showApply)
                cancelButtonVisabilityPick(it.showClear)
                salaryButtonVisabilityPick()
            }
        }

        setControlResultOfPick()
    }

    private fun setControlResultOfPick() {
        parentFragmentManager.setFragmentResultListener(
            FiltersIndustryFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val industry =
                BundleCompat.getParcelable(bundle, FiltersIndustryFragment.INDUSTRY_KEY, SubIndustry::class.java)
            viewModel.setNewIndustry(industry)
        }

        parentFragmentManager.setFragmentResultListener(
            FiltersPlaceOfWorkFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val country =
                BundleCompat.getParcelable(bundle, FiltersPlaceOfWorkFragment.COUNTRY_KEY, Country::class.java)
            val region = BundleCompat.getParcelable(bundle, FiltersPlaceOfWorkFragment.REGION_KEY, Area::class.java)
            viewModel.setNewCounterAndRegion(country, region)
        }
    }

    private fun setButtonListeners() {
        binding.arrowBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.workplace.setOnClickListener {
            val (country, region) = viewModel.getActualCountryAndRegion()
            findNavController().navigate(
                R.id.action_filterFragment_to_filterPlaceOfWorkFragment,
                bundleOf(
                    FiltersCountryFragment.COUNTRY_KEY to country,
                    FiltersRegionFragment.REGION_KEY to region
                )
            )
        }

        binding.industry.setOnClickListener {
            val industryIdPrefs = viewModel.getActualIndustryId()
            findNavController().navigate(
                R.id.action_filterFragment_to_filterIndustryFragment,
                bundleOf(
                    FiltersIndustryFragment.INDUSTRY_KEY to industryIdPrefs
                )
            )
        }

        binding.buttonApply.setOnClickListener {
            binding.buttonApply.gone()
            lifecycleScope.launch(Dispatchers.IO) {
                savePrefs()
                withContext(Dispatchers.Main) {
                    findNavController().popBackStack()
                }
            }
        }

        binding.buttonCancel.setOnClickListener {
            resetFilters()
        }

        binding.buttonClearExpectedSalary.setOnClickListener {
            binding.expectedSalary.setText("")
            hideKeyboard()
        }

        binding.buttonOnlyWithSalary.setOnCheckedChangeListener { _, check ->
            viewModel.setOnlyWithSalary(check)
        }
    }

    private fun setTextListeners() {
        binding.expectedSalary.doOnTextChanged { text, _, _, _ ->
            viewModel.setExpectedSalary(text?.toString())
        }
    }

    private fun setFilters(filterSettings: Filters) {
        setStateLocation(filterSettings.country, filterSettings.region)
        setStateIndustry(filterSettings.industry)

        if (binding.expectedSalary.isFocused.not() &&
            binding.expectedSalary.text?.toString() != filterSettings.expectedSalary
        ) {
            binding.expectedSalary.setText(filterSettings.expectedSalary)
        }
        if (binding.buttonOnlyWithSalary.isChecked != filterSettings.salaryOnlyCheckbox) {
            binding.buttonOnlyWithSalary.isChecked = filterSettings.salaryOnlyCheckbox
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.expectedSalary.windowToken, 0)
    }

    private fun setStateLocation(country: String?, region: String?) {
        if (country?.isNotEmpty() == true) {
            binding.workplaceHolder.visible()
            binding.workplaceArrow.setOnClickListener {
                clearStateLocation()
            }
            binding.workplaceArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1,
                    R.drawable.close_24px
                )
            })
            val textLocation = country + if (region?.isNotEmpty() == true) {
                getString(R.string.divider) + region
            } else {
                ""
            }

            binding.workplace.text = textLocation
        } else {
            binding.workplace.text = ""
            binding.workplaceArrow.setOnClickListener {
                findNavController().navigate(
                    R.id.action_filterFragment_to_filterPlaceOfWorkFragment,
                    bundleOf(
                        FiltersCountryFragment.COUNTRY_KEY to country,
                        FiltersRegionFragment.REGION_KEY to region
                    )
                )
            }
            binding.workplaceArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1,
                    R.drawable.arrow_forward_24px
                )
            })
        }
    }

    private fun clearStateLocation() {
        viewModel.setNewCounterAndRegion(null, null)
        clearArguments(1)
    }

    private fun setStateIndustry(industry: String?) {
        if (industry?.isNotEmpty() == true) {
            binding.industryHolder.visible()
            binding.industryArrow.setOnClickListener {
                clearStateIndustry()
            }
            binding.industry.text = industry
        } else {
            binding.industry.text = ""
            binding.industryArrow.setOnClickListener {
                findNavController().navigate(
                    R.id.action_filterFragment_to_filterIndustryFragment,
                    bundleOf(FiltersIndustryFragment.INDUSTRY_KEY to industry)
                )
            }
        }

        if (binding.industry.text.toString() != "") {
            binding.industryArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1,
                    R.drawable.close_24px
                )
            })
        } else {
            binding.industryArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1,
                    R.drawable.arrow_forward_24px
                )
            })
        }
    }

    private fun clearStateIndustry() {
        viewModel.setNewIndustry(null)
        clearArguments(0)
    }

    private fun resetFilters() {
        binding.buttonCancel.gone()
        viewModel.clearPrefs()
    }

    private suspend fun savePrefs() {
        viewModel.savePrefs()
    }
    private fun saveButtonVisabilityPick(visible: Boolean) {
        if (visible) {
            binding.buttonApply.visible()
        } else {
            binding.buttonApply.gone()
        }
    }

    private fun clearArguments(type: Int) {
        when (type) {
            0 -> {
                binding.industryHolder.gone()
            }

            1 -> {
                binding.workplaceHolder.gone()
            }

        }
    }

    private fun cancelButtonVisabilityPick(visible: Boolean) {
        if (visible) {
            binding.buttonCancel.visible()
        } else {
            binding.buttonCancel.gone()
        }
    }

    private fun salaryButtonVisabilityPick() {
        if (binding.expectedSalary.text?.isEmpty() == true) {
            binding.buttonClearExpectedSalary.gone()
        } else {
            binding.buttonClearExpectedSalary.visible()
        }
    }
}
