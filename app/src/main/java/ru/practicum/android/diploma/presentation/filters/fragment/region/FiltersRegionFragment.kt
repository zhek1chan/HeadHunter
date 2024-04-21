package ru.practicum.android.diploma.presentation.filters.fragment.region

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.marginLeft
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterChooseRegionBinding
import ru.practicum.android.diploma.domain.models.RegionDataItem
import ru.practicum.android.diploma.presentation.filters.adapter.RegionAdapter
import ru.practicum.android.diploma.presentation.filters.fragment.country.FiltersCountryFragment
import ru.practicum.android.diploma.presentation.filters.fragment.main.FiltersFragment
import ru.practicum.android.diploma.presentation.filters.state.region.FiltersRegionsState
import ru.practicum.android.diploma.presentation.filters.viewmodel.region.FiltersRegionViewModel
import ru.practicum.android.diploma.presentation.search.fragment.gone
import ru.practicum.android.diploma.presentation.search.fragment.visible

class FiltersRegionFragment : Fragment() {

    private var _binding: FragmentFilterChooseRegionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FiltersRegionViewModel by viewModel()
    private var regionAdapter: RegionAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterChooseRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.init(arguments?.getString(FiltersFragment.COUNTRY_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(arguments?.getString(FiltersFragment.COUNTRY_KEY))
        viewModel.filtersRegionLiveData.observe(viewLifecycleOwner) {
            render(it)
        }

        regionAdapter = RegionAdapter { region ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(
                    FiltersFragment.REGION_KEY to region.currentRegion, FiltersFragment.COUNTRY_KEY to region.rootRegion
                )
            )
            findNavController().popBackStack()
        }

        binding.regionList.adapter = regionAdapter
        binding.regionList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.choosingRegion.doAfterTextChanged {
            if (it?.isNotEmpty() == true) {
                binding.searchDrawable.setBackgroundResource(R.drawable.close_24px)
                binding.searchDrawable.setOnClickListener {
                    binding.choosingRegion.text?.clear()
                }
                viewModel.findArea(it.toString())
            } else {
                binding.searchDrawable.setBackgroundResource(R.drawable.ic_search)
                viewModel.showArea()
            }

        }

        binding.arrowBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun render(state: FiltersRegionsState) {
        when (state) {
            is FiltersRegionsState.Content -> showContent(state.regions)
            is FiltersRegionsState.Empty -> showEmpty()
            is FiltersRegionsState.Error -> showError()
            is FiltersRegionsState.Loading -> showLoading()
            is FiltersRegionsState.Start -> showStartScreen()
        }
    }

    private fun showStartScreen() {
        binding.progressBar.gone()
        binding.placeholderNoRegionImage.gone()
        binding.placeholderNoRegionText.gone()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = true
    }

    private fun showLoading() {
        binding.progressBar.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = false
    }

    private fun showError() {
        binding.progressBar.gone()
        binding.placeholderNoRegionImage.setImageResource(R.drawable.error_region_list)
        binding.placeholderNoRegionText.setText(R.string.failed_to_get_list)
        binding.placeholderNoRegionImage.visible()
        binding.placeholderNoRegionText.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = false
    }

    private fun showEmpty() {
        binding.progressBar.gone()
        binding.placeholderNoRegionImage.setImageResource(R.drawable.there_no_such_region)
        binding.placeholderNoRegionText.setText(R.string.no_such_region)
        binding.placeholderNoRegionImage.visible()
        binding.placeholderNoRegionText.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = true
    }

    private fun showContent(regions: List<RegionDataItem>) {
        binding.progressBar.gone()
        binding.placeholderNoRegionImage.gone()
        binding.placeholderNoRegionText.gone()
        regionAdapter!!.setItems(regions)
        binding.regionList.visible()
        binding.choosingRegion.isEnabled = true
    }

    companion object {
        const val REQUEST_KEY = "REGION_KEY"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
