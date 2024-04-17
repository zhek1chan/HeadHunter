package ru.practicum.android.diploma.presentation.filters.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.SubIndustry
import ru.practicum.android.diploma.presentation.filters.fragment.industry.FiltersIndustryFragment

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

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

        parentFragmentManager.setFragmentResultListener(
            FiltersIndustryFragment.INDUSTRY_KEY,
            viewLifecycleOwner,
        ){ _, bundle ->
            val industry =
                BundleCompat.getParcelable(bundle, FiltersIndustryFragment.INDUSTRY, Industry::class.java)
            // TODO Здесь будет логика работы с viewModel
            if (industry != null) {
                binding.industryValue.text = industry.name
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
