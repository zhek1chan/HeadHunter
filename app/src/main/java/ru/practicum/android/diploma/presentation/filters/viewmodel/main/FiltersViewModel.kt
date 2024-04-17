package ru.practicum.android.diploma.presentation.filters.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filters.FiltersInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.SubIndustry
import ru.practicum.android.diploma.domain.models.checkEmpty

class FiltersViewModel(
    private val filterInteractor: FiltersInteractor,
) : ViewModel() {
    val filterState = MutableStateFlow(StateFilters())
    private var filter: Filters? = null
        set(value) {
            field = value
            if (field != null) {
                controlChangeFilter()
            }
        }
    private var oldFilter: Filters? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            oldFilter = getPrefs()
            filter = oldFilter?.copy()
        }
    }

    fun controlChangeFilter() {
        viewModelScope.launch(Dispatchers.Main) {
            val showApply = filter != oldFilter
            filterState.emit(
                filterState.value.copy(
                    filters = filter!!,
                    showApply = showApply,
                    showClear = showApply || filter?.checkEmpty() == false
                )
            )
        }
    }

    fun setNewCounterAndRegion(country: Country?, region: Area?) {
        filter = filter?.copy(
            country = country?.name ?: "",
            countryId = country?.id ?: "",
            region = region?.name ?: "",
            regionId = region?.id ?: ""
        )
    }

    fun setNewIndustry(industry: SubIndustry?) {
        filter = filter?.copy(
            industry = industry?.name ?: "",
            industryId = industry?.id ?: "",
        )
    }

    fun setExpectedSalary(expectedSalary: String?) {
        filter = filter?.copy(
            expectedSalary = expectedSalary ?: "",
        )
    }

    fun setOnlyWithSalary(salaryOnlyCheckbox: Boolean) {
        filter = filter?.copy(
            salaryOnlyCheckbox = salaryOnlyCheckbox,
        )
    }

    fun getActualCountryAndRegion(): Pair<Country?, Area?> {
        return if (filter?.country?.isNotEmpty() == true) {
            val country = Country(id = filter!!.countryId, name = filter!!.country, parentId = "")
            val area = if (filter?.region?.isNotEmpty() == true) {
                Area(id = filter!!.regionId, name = filter!!.region, parentId = "", areas = listOf())
            } else {
                null
            }
            country to area
        } else {
            null to null
        }
    }

    fun getActualIndustryId(): String? {
        return filter?.industryId
    }

    fun getPrefs(): Filters = filterInteractor.getPrefs()
    suspend fun savePrefs() {
        filter?.let { filterInteractor.savePrefs(it) }
    }

    fun clearPrefs() {
        viewModelScope.launch(Dispatchers.IO) {
            filterInteractor.clearPrefs()
            oldFilter = getPrefs()
            filter = oldFilter?.copy()
        }
    }
}

data class StateFilters(
    val filters: Filters = Filters("", "", "", "", "", "", "", false),
    val showApply: Boolean = false,
    val showClear: Boolean = false
)
