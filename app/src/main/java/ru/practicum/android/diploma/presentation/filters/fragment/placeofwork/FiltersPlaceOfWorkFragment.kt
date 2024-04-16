package ru.practicum.android.diploma.presentation.filters.fragment.placeofwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterChoosePlaceOfWorkBinding
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.presentation.filters.fragment.country.FiltersCountryFragment
import ru.practicum.android.diploma.presentation.filters.fragment.region.FiltersRegionFragment
import ru.practicum.android.diploma.presentation.search.fragment.gone
import ru.practicum.android.diploma.presentation.search.fragment.visible

class FiltersPlaceOfWorkFragment : Fragment() {

    private var _binding: FragmentFilterChoosePlaceOfWorkBinding? = null
    private val binding get() = _binding!!
    private var country: Country? = null
    private var region: Area? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterChoosePlaceOfWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListeners()
        initFilters()
        if (country != null) {
            binding.textView2.visible()
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
            findNavController().navigate(
                R.id.action_filterPlaceOfWorkFragment_to_filtersRegionFragment,
                bundleOf(FiltersCountryFragment.COUNTRY_KEY to country?.id)
            )
        }

        binding.textView2.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(
                    COUNTRY_KEY to country, REGION_KEY to region
                )
            )
            findNavController().popBackStack()
        }
    }

    fun initView() {
        country = country ?: arguments?.getParcelable(FiltersCountryFragment.COUNTRY_KEY)
        region = region ?: arguments?.getParcelable(FiltersRegionFragment.REGION_KEY)
        setRegion(region)
        setCountry(country)
    }

    private fun initFilters() {
        parentFragmentManager.setFragmentResultListener(
            FiltersCountryFragment.REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            clearArguments()
            val newCountry = bundle.getParcelable<Country>(FiltersCountryFragment.COUNTRY_KEY)
            setCountry(newCountry)
            val checkNewCountry = newCountry != country
            country = newCountry
            if (checkNewCountry) {
                region = null
                setRegion(null)
            }
        }

        parentFragmentManager.setFragmentResultListener(
            FiltersRegionFragment.REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            clearArguments()
            region = bundle.getParcelable(FiltersRegionFragment.REGION_KEY)
            country = bundle.getParcelable<Area?>(FiltersCountryFragment.COUNTRY_KEY)?.let {
                Country(
                    id = it.id, parentId = it.parentId ?: "", name = it.name
                )
            } ?: country
            setRegion(region)
            setCountry(country)
        }
    }

    fun setCountry(actualCountry: Country?) {
        if (actualCountry != null) {
            binding.country.setText(actualCountry.name)
            binding.textView2.visible()
            binding.placeOfWorkArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1, R.drawable.close_24px
                )
            })
            binding.placeOfWorkArrow.setOnClickListener {
                clearArguments()
                country = null
                region = null
                setCountry(null)
                setRegion(null)
            }
        } else {
            binding.country.setText("")
            binding.textView2.gone()
            binding.placeOfWorkArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1, R.drawable.arrow_forward_24px
                )
            })
            binding.placeOfWorkArrow.setOnClickListener(null)
        }
    }

    fun setRegion(actualArea: Area?) {
        if (actualArea != null) {
            binding.region.setText(actualArea.name)
            binding.textView2.visible()
            binding.regionArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1, R.drawable.close_24px
                )
            })
            binding.regionArrow.setOnClickListener {
                clearArguments()
                region = null
                setRegion(null)
            }
        } else {
            binding.region.setText("")
            if (country == null) {
                binding.textView2.gone()
            }
            binding.regionArrow.setImageDrawable(context?.let { it1 ->
                AppCompatResources.getDrawable(
                    it1, R.drawable.arrow_forward_24px
                )
            })
            binding.regionArrow.setOnClickListener(null)
        }
    }

    fun clearArguments() {
        arguments = null
    }

    companion object {
        const val REQUEST_KEY = "PLACE_KEY"
        const val COUNTRY_KEY = "COUNTRY"
        const val REGION_KEY = "REGION"
    }
}
