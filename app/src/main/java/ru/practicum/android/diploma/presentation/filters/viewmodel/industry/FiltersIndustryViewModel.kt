package ru.practicum.android.diploma.presentation.filters.viewmodel.industry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.data.Constant
import ru.practicum.android.diploma.domain.filters.FiltersInteractor
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.SubIndustry
import ru.practicum.android.diploma.presentation.filters.state.industry.RequestIndustriesState
import ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview.IndustriesAdapterItem
import ru.practicum.android.diploma.presentation.filters.state.industry.FiltersIndustryState

class FiltersIndustryViewModel(private val interactor: FiltersInteractor) : ViewModel() {
    private val _state = MutableLiveData<RequestIndustriesState>()
    val state: LiveData<RequestIndustriesState> = _state
    private val _industryState = MutableLiveData<FiltersIndustryState>()
    val industryState: LiveData<FiltersIndustryState> = _industryState
    private var list = ArrayList<SubIndustry>()

    fun getIndustries() {
        _state.value = RequestIndustriesState.Loading
        viewModelScope.launch {
            interactor.getIndustries().collect { industry ->
                processResult(industry)
            }
        }
    }

    private fun processResult(industry: Resource<List<Industry>>) {
        if (industry.code == Constant.SUCCESS_RESULT_CODE) {
            if (industry.data != null) {
                list.clear()
                list.addAll(sortIndustries(industry.data))
                _state.value =
                    RequestIndustriesState.Success(list.map { IndustriesAdapterItem(it) })
            } else {
                _state.value = RequestIndustriesState.Empty
            }
        } else {
            _state.value = RequestIndustriesState.Error
        }
    }

    private fun sortIndustries(industriesList: List<Industry>): List<SubIndustry> {
        val sortedSubIndustriesList: MutableList<SubIndustry> = mutableListOf()
        for (industry in industriesList) {
            for (subindustry in industry.industries) {
                sortedSubIndustriesList.add(
                    SubIndustry(
                        id = subindustry.id,
                        name = subindustry.name
                    )
                )
            }
            sortedSubIndustriesList.add(
                SubIndustry(
                    id = industry.id,
                    name = industry.name
                )
            )
        }

        return sortedSubIndustriesList.sortedBy { it.name }
    }

    fun filterIndustries(editText: String) {
        if (editText.isNotEmpty()) {
            val filteredList = list.filter {
                it.name.contains(editText, ignoreCase = true)
            }
            if (filteredList.isNotEmpty()) {
                _state.value = RequestIndustriesState.Success(filteredList.map { IndustriesAdapterItem(it) })
            } else {
                _state.value = RequestIndustriesState.Empty
            }
        } else {
            _state.value =
                RequestIndustriesState.Success(list.map { IndustriesAdapterItem(it) })
        }
    }

    fun setCurrentIndustry(industry: SubIndustry?) {
        _industryState.value = FiltersIndustryState.CurrentIndustry(industry)
    }

    fun setIndustryId(id: String?) {
        _industryState.value = FiltersIndustryState.IndustryId(id)
    }

    fun setIndustryIndex(index: Int) {
        _industryState.value = FiltersIndustryState.IndustryIndex(index)
    }
}
