package ru.practicum.android.diploma.presentation.filters.fragment.region

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.databinding.FragmentFilterChooseRegionBinding

class FiltersRegionFragment : Fragment() {

    private var _binding: FragmentFilterChooseRegionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterChooseRegionBinding.inflate(inflater, container, false)
        return binding.root
    }
}
