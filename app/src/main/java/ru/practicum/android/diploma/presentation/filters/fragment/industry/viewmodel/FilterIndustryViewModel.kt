package ru.practicum.android.diploma.presentation.filters.fragment.industry.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.Constants.SUCCESS_RESULT_CODE
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.domain.filters.FiltersInteractor
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.presentation.filters.fragment.industry.RequestIndustriesState

class FilterIndustryViewModel(
    private val filtersInteractor: FiltersInteractor
) : ViewModel() {
    private val _industriesState = MutableLiveData<RequestIndustriesState>()
    private var _chosenIndustry = MutableLiveData<Industry>()

    fun getIndustriesState(): LiveData<RequestIndustriesState> = _industriesState
    fun getChosenIndustry(): LiveData<Industry> = _chosenIndustry

    private var _currentIndustries = listOf<Industry>()

    init {
        fetchIndustries()
    }

    fun fetchIndustries() {
        _industriesState.value = RequestIndustriesState.Loading
        viewModelScope.launch {
            filtersInteractor.getIndustries().collect { industriesResponse ->
                    processResult(industriesResponse)
                }
        }
    }

    fun clickIndustry(industry: Industry) {
        _chosenIndustry.value = if (industry.id != (_chosenIndustry.value?.id ?: "")) industry else null
        if (_industriesState.value is RequestIndustriesState.Success) {
            (_industriesState.value as RequestIndustriesState.Success).data.forEach { item ->
                if (item.id == industry.id) {
                    item.isChosen = !industry.isChosen
                } else {
                    item.isChosen = false
                }
            }
        }
    }


    fun filterIndustries(text: String) {
        if (text.isNotEmpty()) {
            val filteredList = _currentIndustries.filter {
                it.name.contains(text, ignoreCase = true)
            }
            if (filteredList.isNotEmpty()) {
                _industriesState.value = RequestIndustriesState.Success(filteredList)
            } else {
                _industriesState.value = RequestIndustriesState.Empty
            }
        } else {
            fetchIndustries()
        }
    }

    private fun processResult(response: Resource<List<Industry>>) {
        if (response.code == SUCCESS_RESULT_CODE) {
            if (!response.data.isNullOrEmpty()) {
                _currentIndustries = response.data
                _industriesState.value = RequestIndustriesState.Success(response.data)
                checkChosenIndustry()
            } else {
                _industriesState.value = RequestIndustriesState.Empty
            }
        } else {
            _industriesState.value = RequestIndustriesState.Error
        }
    }

    private fun checkChosenIndustry() {
        if (_industriesState.value is RequestIndustriesState.Success && _chosenIndustry.value != null) {
            (_industriesState.value as RequestIndustriesState.Success).data.forEach { item ->
                item.isChosen = item.id == _chosenIndustry.value!!.id
            }
        }
    }

}
