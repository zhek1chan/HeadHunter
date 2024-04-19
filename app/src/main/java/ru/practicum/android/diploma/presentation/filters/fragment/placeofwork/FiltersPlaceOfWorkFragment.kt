package ru.practicum.android.diploma.presentation.filters.fragment.placeofwork

import FiltersPlaceOfWorkViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterChoosePlaceOfWorkBinding
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.presentation.filters.fragment.country.FiltersCountryFragment
import ru.practicum.android.diploma.presentation.filters.fragment.region.FiltersRegionFragment
import ru.practicum.android.diploma.presentation.search.fragment.gone
import ru.practicum.android.diploma.presentation.search.fragment.visible

class FiltersPlaceOfWorkFragment : Fragment() {

    private lateinit var binding: FragmentFilterChoosePlaceOfWorkBinding
    private val viewModel by viewModel<FiltersPlaceOfWorkViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterChoosePlaceOfWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListeners()
        initFilters()
        observeSelectedCountry()
    }

    private fun initView() {
        with(binding) {
            arrowBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
            country.setOnClickListener {
                navigateToCountryFragment()
            }
            region.setOnClickListener {
                navigateToRegionFragment()
            }
            buttonPick.setOnClickListener {
                handlePickButtonClick()
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            arrowBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
            country.setOnClickListener {
                navigateToCountryFragment()
            }
            region.setOnClickListener {
                navigateToRegionFragment()
            }
            buttonPick.setOnClickListener {
                handlePickButtonClick()
            }
        }
    }

    private fun initFilters() {
        parentFragmentManager.setFragmentResultListener(
            FiltersCountryFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val newCountry = bundle.getParcelable<Country>(FiltersCountryFragment.COUNTRY_KEY)
            viewModel.setSelectedCountry(newCountry)
            if (newCountry != viewModel.selectedCountry.value) {
                viewModel.setSelectedRegion(null)
                updateRegionUI(null, newCountry)
            }
        }

        parentFragmentManager.setFragmentResultListener(
            FiltersRegionFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val newRegion = bundle.getParcelable<Area>(FiltersRegionFragment.REGION_KEY)
            viewModel.setSelectedRegion(newRegion)
            if (newRegion != viewModel.selectedRegion.value) {
                updateRegionUI(newRegion, null)
            }
        }
    }


    private fun observeSelectedCountry() {
        viewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            updateCountryUI(country)
            updateRegionUI(viewModel.selectedRegion.value, country)
        }
    }

    private fun updateCountryUI(countryValue: Country?) {
        if (countryValue != null) {
            with(binding) {
                countryTop.visible()
                country.text = countryValue.name
                buttonPick.visible()
                workplaceArrow.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.close_24px
                    )
                })
                workplaceArrow.setOnClickListener {
                    viewModel.clearSelectedFilters()
                    viewModel.setSelectedCountry(null)
                    viewModel.setSelectedRegion(null)
                }
            }
        } else {
            with(binding) {
                country.text = ""
                buttonPick.gone()
                countryTop.gone()
                workplaceArrow.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.arrow_forward_24px
                    )
                })
                workplaceArrow.setOnClickListener {
                    navigateToCountryFragment()
                }
            }
        }
    }

    private fun updateRegionUI(regionValue: Area?, countryValue: Country?) {
        if (regionValue != null) {
            with(binding) {
                regionTop.visible()
                region.text = regionValue.name
                buttonPick.visible()
                regionArrow.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.close_24px
                    )
                })
                regionArrow.setOnClickListener {
                    viewModel.clearSelectedFilters()
                    viewModel.setSelectedRegion(null)
                }
            }
        } else {
            with(binding) {
                region.text = ""
                if (countryValue == null) {
                    buttonPick.gone()
                }
                regionTop.gone()
                regionArrow.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.arrow_forward_24px
                    )
                })
                regionArrow.setOnClickListener {
                    navigateToRegionFragment()
                }
            }
        }
    }


    private fun navigateToCountryFragment() {
        findNavController().navigate(R.id.action_filterPlaceOfWorkFragment_to_filtersCountryFragment)
    }

    private fun navigateToRegionFragment() {
        viewModel.selectedCountry.value?.let { country ->
            findNavController().navigate(
                R.id.action_filterPlaceOfWorkFragment_to_filtersRegionFragment,
                bundleOf(FiltersCountryFragment.COUNTRY_KEY to country.id)
            )
        }
    }

    private fun handlePickButtonClick() {
        val country = viewModel.selectedCountry.value
        val region = viewModel.selectedRegion.value
        if (country != null) {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    COUNTRY_KEY to country,
                    REGION_KEY to region
                )
            )
            findNavController().popBackStack()
        }
    }

    companion object {
        const val REQUEST_KEY = "PLACE_KEY"
        const val COUNTRY_KEY = "COUNTRY"
        const val REGION_KEY = "REGION"
    }
}

