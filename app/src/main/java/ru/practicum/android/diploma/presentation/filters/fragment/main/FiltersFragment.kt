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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.presentation.filters.fragment.country.FiltersCountryFragment
import ru.practicum.android.diploma.presentation.filters.fragment.region.FiltersRegionFragment
import ru.practicum.android.diploma.presentation.filters.fragment.industry.FiltersIndustryFragment
import ru.practicum.android.diploma.presentation.filters.viewmodel.main.FiltersViewModel
import ru.practicum.android.diploma.presentation.search.fragment.gone
import ru.practicum.android.diploma.presentation.search.fragment.visible
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.SubIndustry
import ru.practicum.android.diploma.presentation.filters.fragment.placeofwork.FiltersPlaceOfWorkFragment

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FiltersViewModel>()
    private var industryIdPrefs = ""

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

        initButtonListeners()
        initTextListeners()

        binding.industry.setOnClickListener {
            findNavController().navigate(R.id.chooseIndustryFragment)
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
    }

    fun setFragmentResultListenerControl() {
        /*parentFragmentManager.setFragmentResultListener(
            FiltersIndustryFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val industry =
                BundleCompat.getParcelable(bundle, FiltersIndustryFragment.INDUSTRY_KEY, SubIndustry::class.java)
            viewModel.setNewIndustry(industry)
        }*/

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

    private fun initButtonListeners() {
        binding.backButton.setOnClickListener {
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

        /*binding.industry.setOnClickListener {
            val industryIdPrefs = viewModel.getActualIndustryId()
            findNavController().navigate(
                R.id.action_filterFragment_to_filterIndustryFragment,
                bundleOf(
                    FiltersIndustryFragment.INDUSTRY_KEY_ID to industryIdPrefs
                )
            )
        }*/

        binding.apply.setOnClickListener {
            binding.apply.gone()
            lifecycleScope.launch(Dispatchers.IO) {
                savePrefs()
                withContext(Dispatchers.Main) {
                    findNavController().popBackStack()
                }
            }
        }

        binding.remove.setOnClickListener {
            resetFilters()
        }

        binding.clearButton.setOnClickListener {
            binding.expectedSalary.setText("")
            hideKeyboard()
        }

        binding.salaryOnlyCheckbox.setOnCheckedChangeListener { button, check ->
            viewModel.setSalaryOnlyCheckbox(check)
        }
    }

    private fun initTextListeners() {
        binding.expectedSalary.doOnTextChanged { text, start, before, count ->
            viewModel.setExpectedSalary(text?.toString())
        }
    }

    private fun initFilterSettings(filterSettings: Filters) {
        setStateLocation(filterSettings.country, filterSettings.region)
        //setStateIndustry(filterSettings.industry)

        if (binding.expectedSalary.isFocused.not() &&
            binding.expectedSalary.text?.toString() != filterSettings.expectedSalary
        ) {
            binding.expectedSalary.setText(filterSettings.expectedSalary)
        }
        if (binding.salaryOnlyCheckbox.isChecked != filterSettings.salaryOnlyCheckbox) {
            binding.salaryOnlyCheckbox.isChecked = filterSettings.salaryOnlyCheckbox
        }
    }

    private suspend fun savePrefs() {
        viewModel.savePrefs()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.expectedSalary.windowToken, 0)
    }

    fun setStateLocation(country: String?, region: String?) {
        if (country?.isNotEmpty() == true) {
            //binding.placeOfWorkTop.visible()
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
            binding.workplaceValue.setText(textLocation)
        } else {
            //binding.workplaceValue.setText("dfgfgfg")
            binding.workplaceView.setOnClickListener {
                findNavController().navigate(
                    R.id.action_filterFragment_to_filterPlaceOfWorkFragment,
                    bundleOf(
                        FiltersCountryFragment.COUNTRY_KEY to country,
                        FiltersRegionFragment.REGION_KEY to region
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

    fun clearStateLocation() {
        viewModel.setNewCounterAndRegion(null, null)
        //clearArguments(1)
    }

    /*fun setStateIndustry(industry: String?) {
        if (industry?.isNotEmpty() == true) {
            binding.industryTop.visible()
            binding.industryView.setOnClickListener {
                clearStateIndustry()
            }
            binding.industryValue.setText(industry)
        } else {
            binding.industryValue.setText("")
            binding.industryView.setOnClickListener {
                findNavController().navigate(
                    R.id.action_filterFragment_to_filterIndustryFragment,
                    bundleOf(FiltersIndustryFragment.INDUSTRY_KEY to industry)
                )
            }
        }

        if (binding.industryValue.text.toString() != "") {
            binding.industryView.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1,
                    R.drawable.close_24px
                )
            })
        } else {
            binding.industryView.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1,
                    R.drawable.arrow_forward_24px
                )
            })
        }
    }*/

    fun clearStateIndustry() {
        viewModel.setNewIndustry(null)
        //clearArguments(0)
    }

    private fun resetFilters() {
        binding.remove.gone()
        viewModel.clearPrefs()
    }

    fun visibleSaveControl(visible: Boolean) {
        if (visible) {
            binding.apply.visible()
        } else {
            binding.apply.gone()
        }
    }

    /*fun clearArguments(type: Int) {
        when (type) {
            0 -> {
                binding.industryTop.gone()
            }
            1 -> {
                binding.placeOfWorkTop.gone()
            }

        }
    }*/

    fun visibleClearControl(visible: Boolean) {
        if (visible) {
            binding.remove.visible()
        } else {
            binding.remove.gone()
        }
    }

    fun visibleClearSalaryButtonControl() {
        if (binding.expectedSalary.text?.isEmpty() == true) {
            binding.clearButton.gone()
        } else {
            binding.clearButton.visible()
        }
    }
}
