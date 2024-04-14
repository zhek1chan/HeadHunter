package ru.practicum.android.diploma.presentation.filters.viewmodel.country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.data.Constant
import ru.practicum.android.diploma.domain.filters.FiltersInteractor
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.CountrySortOrder
import ru.practicum.android.diploma.presentation.filters.state.country.FiltersCountriesState

class FiltersCountryViewModel(
    private val filtersInteractor: FiltersInteractor,
) : ViewModel() {
    private var countries = ArrayList<Country>()
    private val filtersCountriesLiveData = MutableLiveData<FiltersCountriesState>()
    fun getFiltersCountriesLiveData(): LiveData<FiltersCountriesState> = filtersCountriesLiveData
    init {
        filtersCountriesLiveData.postValue(FiltersCountriesState.Loading)
    }
    fun fillData() {
        viewModelScope.launch(Dispatchers.IO) {
            filtersInteractor.getCountries().collect { country ->
                loadCountries(country)
            }
        }
    }

    private fun loadCountries(foundCountries: Resource<List<Country>>) {
        if (foundCountries.code == Constant.SUCCESS_RESULT_CODE) {
            if (foundCountries.data != null) {
                var countriesList = CountrySortOrder.sortCountriesListManually(foundCountries.data)

                countries.clear()
                countries.addAll(countriesList)
                filtersCountriesLiveData.postValue(FiltersCountriesState.Content(countries))
            } else {
                filtersCountriesLiveData.postValue(FiltersCountriesState.Empty)
            }
        } else {
            filtersCountriesLiveData.postValue(FiltersCountriesState.Error)
        }
    }
}
