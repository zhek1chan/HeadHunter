package ru.practicum.android.diploma.presentation.filters.fragment.placeofwork

import FiltersPlaceOfWorkViewModel
import android.os.Bundle
import android.util.Log
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
import ru.practicum.android.diploma.presentation.filters.fragment.main.FiltersFragment
import ru.practicum.android.diploma.presentation.filters.fragment.region.FiltersRegionFragment
import ru.practicum.android.diploma.presentation.search.fragment.gone
import ru.practicum.android.diploma.presentation.search.fragment.visible

class FiltersPlaceOfWorkFragment : Fragment() {

    private var _binding: FragmentFilterChoosePlaceOfWorkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FiltersPlaceOfWorkViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterChoosePlaceOfWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListeners()
        initFilters()
        viewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            if (country != null) {
                binding.buttonPick.visibility = View.VISIBLE
                setCountry(country)
                viewModel.selectedRegion.observe(viewLifecycleOwner, ::setRegion)
            } else {
                binding.buttonPick.visibility = View.GONE
                setRegion(null)
            }
        }
    }

    private fun initListeners() {
        binding.arrowBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.country.setOnClickListener {
            findNavController().navigate(R.id.action_filterPlaceOfWorkFragment_to_filtersCountryFragment)
        }

        binding.region.setOnClickListener {
            if (binding.country.text.isEmpty()) {
                findNavController().navigate(
                    R.id.action_filterPlaceOfWorkFragment_to_filtersRegionFragment,
                    bundleOf(FiltersFragment.COUNTRY_KEY to null)
                )
            } else {
                viewModel.selectedCountry.value?.let { country ->
                    findNavController().navigate(
                        R.id.action_filterPlaceOfWorkFragment_to_filtersRegionFragment,
                        bundleOf(FiltersFragment.COUNTRY_KEY to country.id)
                    )
                }
            }
        }

        binding.buttonPick.setOnClickListener {
            viewModel.selectedCountry.value?.let { country ->
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        FiltersFragment.COUNTRY_KEY to country,
                        FiltersFragment.REGION_KEY to viewModel.selectedRegion.value
                    )
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun initView() {
        Log.d("Bundle region", "${arguments?.getString(FiltersFragment.REGION_KEY)}")
        Log.d("Bundle country", "${arguments?.getString(FiltersFragment.COUNTRY_KEY)}")
        if (arguments?.getParcelable(FiltersFragment.COUNTRY_KEY) as? Country != null) {
            viewModel.setSelectedCountry(arguments?.getParcelable(FiltersFragment.COUNTRY_KEY))
        }
        if (arguments?.getParcelable(FiltersFragment.REGION_KEY) as? Area != null) {
            viewModel.setSelectedRegion(arguments?.getParcelable(FiltersFragment.REGION_KEY))
        }
    }

    private fun initFilters() {
        parentFragmentManager.setFragmentResultListener(
            FiltersCountryFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val newCountry = bundle.getParcelable<Country>(FiltersFragment.COUNTRY_KEY)
            viewModel.setSelectedCountry(newCountry)
            if (newCountry != null && newCountry != viewModel.selectedCountry.value) {
                viewModel.setSelectedRegion(null)
            }
        }

        parentFragmentManager.setFragmentResultListener(
            FiltersRegionFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val newRegion = bundle.getParcelable<Area>(FiltersFragment.REGION_KEY)
            viewModel.setSelectedRegion(newRegion)
            val newCountry = bundle.getParcelable<Area?>(FiltersFragment.COUNTRY_KEY)?.let {
                Country(
                    id = it.id, parentId = it.parentId ?: "", name = it.name
                )
            }
            viewModel.setSelectedCountry(newCountry)
        }
    }

    private fun setCountry(actualCountry: Country?) {
        if (actualCountry != null) {
            binding.countryTop.visible()
            binding.country.text = actualCountry.name
            binding.workplaceArrow.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.close_changing_color_24px
                )
            )
            binding.workplaceArrow.setOnClickListener {
                viewModel.setSelectedCountry(null)
                viewModel.setSelectedRegion(null)
                binding.country.text = ""
                binding.region.text = ""
                binding.workplaceArrow.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.arrow_forward_24px
                    )
                )
            }
        } else {
            binding.country.text = ""
            binding.countryTop.gone()
            binding.workplaceArrow.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.arrow_forward_24px
                )
            )
            binding.workplaceArrow.setOnClickListener {
                findNavController().navigate(R.id.action_filterPlaceOfWorkFragment_to_filtersCountryFragment)
            }
        }
    }

    private fun setRegion(actualArea: Area?) {
        if (actualArea != null) {
            binding.regionTop.visible()
            binding.region.text = actualArea.name
            binding.regionArrow.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.close_changing_color_24px
                )
            )
            binding.regionArrow.setOnClickListener {
                binding.region.text = ""
                viewModel.setSelectedRegion(null)
            }
        } else {
            binding.region.text = ""
            binding.regionTop.gone()
            binding.regionArrow.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.arrow_forward_24px
                )
            )
            binding.regionArrow.setOnClickListener {
                viewModel.selectedCountry.value?.let { country ->
                    findNavController().navigate(
                        R.id.action_filterPlaceOfWorkFragment_to_filtersRegionFragment,
                        bundleOf(FiltersFragment.COUNTRY_KEY to country.id)
                    )
                }
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "PLACE_KEY"
    }
}
