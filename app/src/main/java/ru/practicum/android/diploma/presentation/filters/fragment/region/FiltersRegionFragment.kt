package ru.practicum.android.diploma.presentation.filters.fragment.region

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterChooseRegionBinding
import ru.practicum.android.diploma.domain.models.RegionDataItem
import ru.practicum.android.diploma.presentation.filters.fragment.country.FiltersCountryFragment
import ru.practicum.android.diploma.presentation.filters.viewmodel.region.FiltersRegionViewModel

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
        viewModel.init(arguments?.getString(FiltersCountryFragment.COUNTRY_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.filtersRegionLiveData.observe(viewLifecycleOwner) {
            render(it)
        }

        regionAdapter = RegionAdapter { region ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    REGION_KEY to region.currentRegion,
                    FiltersCountryFragment.COUNTRY_KEY to region.rootRegion
                )
            )
            findNavController().popBackStack()
        }

        binding.regionList.adapter = regionAdapter
        binding.regionList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.choosingRegion.doAfterTextChanged {
            if (it?.isNotEmpty() == true) {
                binding.imageTextView.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.ic_clear_button
                    )
                })
                viewModel.findArea(it?.toString() ?: "")
            } else {
                binding.imageTextView.setImageDrawable(context?.let { it1 ->
                    AppCompatResources.getDrawable(
                        it1,
                        R.drawable.ic_search
                    )
                })
                viewModel.showAllArea()
            }

        }

        binding.arrowBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.imageTextView.setOnClickListener {
            binding.choosingRegion.setText("")
            viewModel.showArea()
        }

        binding.choosingRegion.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = context?.let { ContextCompat.getSystemService(it, InputMethodManager::class.java) }
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
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

    fun showStartScreen() {
        binding.progressBar.gone()
        binding.placeholderImage.gone()
        binding.placeholderText.gone()
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
        binding.placeholderImage.setImageResource(R.drawable.error_region_list)
        binding.placeholderText.setText(R.string.failed_to_get_list)
        binding.placeholderImage.visible()
        binding.placeholderText.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = false
    }

    private fun showEmpty() {
        binding.progressBar.gone()
        binding.placeholderImage.setImageResource(R.drawable.there_no_such_region)
        binding.placeholderText.setText(R.string.no_region)
        binding.placeholderImage.visible()
        binding.placeholderText.visible()
        binding.regionList.gone()
        binding.choosingRegion.isEnabled = true
    }

    private fun showContent(regions: List<RegionDataItem>) {
        binding.progressBar.gone()
        binding.placeholderImage.gone()
        binding.placeholderText.gone()
        regionAdapter!!.setItems(regions)
        binding.regionList.visible()
        binding.choosingRegion.isEnabled = true
    }

    companion object {
        const val REQUEST_KEY = "REGION_KEY"
        const val REGION_KEY = "REGION"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
