package ru.practicum.android.diploma.presentation.filters.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.Industry
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
    private val viewModel by viewModel<FiltersViewModel>()

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

        binding.industry.setOnClickListener {
            findNavController().navigate(R.id.action_filterFragment_to_filterIndustryFragment)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.filterState.collect {
                initFilterSettings(it.filters)
                visibleSaveControl(it.showApply)
                visibleClearControl(it.showClear)
                visibleClearSalaryButtonControl()
            }
        }

        setFragmentResultListenerControl()

        parentFragmentManager.setFragmentResultListener(
            FiltersIndustryFragment.INDUSTRY_KEY,
            viewLifecycleOwner,
        ) { _, bundle ->
            val industry = BundleCompat.getParcelable(bundle, FiltersIndustryFragment.INDUSTRY, Industry::class.java)
            if (industry != null) {
                binding.industryValue.text = industry.name
            }
        }
    }

    private fun setFragmentResultListenerControl() {
        parentFragmentManager.setFragmentResultListener(
            FiltersPlaceOfWorkFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val country = BundleCompat.getParcelable(
                bundle,
                FiltersPlaceOfWorkFragment.COUNTRY_KEY,
                Country::class.java
            )
            val region = BundleCompat.getParcelable(
                bundle,
                FiltersPlaceOfWorkFragment.REGION_KEY,
                Area::class.java
            )
            viewModel.setNewCounterAndRegion(country, region)
        }
    }

    private fun initFilterSettings(filterSettings: Filters) {
        setStateLocation(filterSettings.country, filterSettings.region)
        if (binding.expectedSalary.isFocused.not() &&
            binding.expectedSalary.text?.toString() != filterSettings.expectedSalary) {
            binding.expectedSalary.setText(filterSettings.expectedSalary)
        }
        if (binding.salaryOnlyCheckbox.isChecked != filterSettings.salaryOnlyCheckbox) {
            binding.salaryOnlyCheckbox.isChecked = filterSettings.salaryOnlyCheckbox
        }
    }

    private fun setStateLocation(country: String?, region: String?) {
        if (country?.isNotEmpty() == true) {
            binding.workplaceView.setOnClickListener {
                clearStateLocation()
            }
            binding.workplaceView.setImageDrawable(context?.let { it1 ->
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
            binding.workplaceValue.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1,
                    R.color.filters_values_text_color
                )
            })
            binding.workplaceValue.text = textLocation
        } else {
            binding.workplaceValue.setText(R.string.workplace)
            binding.workplaceValue.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1,
                    R.color.gray
                )
            })
            binding.workplaceView.setOnClickListener {
                findNavController().navigate(
                    R.id.action_filterFragment_to_filterPlaceOfWorkFragment, bundleOf(
                        FiltersCountryFragment.COUNTRY_KEY to country, FiltersRegionFragment.REGION_KEY to region
                    )
                )
            }
            binding.workplaceView.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1,
                    R.drawable.arrow_forward_24px
                )
            })
        }
    }

    private fun clearStateLocation() {
        viewModel.setNewCounterAndRegion(null, null)
    }

    private fun visibleSaveControl(visible: Boolean) {
        if (visible) {
            binding.buttonApply.visible()
        } else {
            binding.buttonApply.gone()
        }
    }

    private fun visibleClearControl(visible: Boolean) {
        if (visible) {
            binding.buttonRemove.visible()
        } else {
            binding.buttonRemove.gone()
        }
    }

    private fun visibleClearSalaryButtonControl() {
        if (binding.expectedSalary.text?.isEmpty() == true) {
            binding.clearButton.gone()
        } else {
            binding.clearButton.visible()
        }
    }
}
