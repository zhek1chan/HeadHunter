package ru.practicum.android.diploma.presentation.filters.viewmodel.region

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.filters.FiltersInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.RegionDataItem
import ru.practicum.android.diploma.presentation.filters.state.region.FiltersRegionsState
import java.text.RuleBasedCollator

class FiltersRegionViewModel(
    private val filtersInteractor: FiltersInteractor,
) : ViewModel() {

    val filtersRegionLiveData = MutableLiveData<FiltersRegionsState>()
    private val allAreas = mutableListOf<Area>()

    init {
        filtersRegionLiveData.postValue(FiltersRegionsState.Start)
    }

    fun init(country: String?) {
        if (allAreas.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                filtersRegionLiveData.postValue(FiltersRegionsState.Loading)
                loadRegions(country)
            }
        }
    }

    private suspend fun loadRegions(idArea: String?) {
        filtersInteractor.getRegions(idArea)
            .collect {
                if (it.data.isNullOrEmpty()) {
                    filtersRegionLiveData.postValue(FiltersRegionsState.Error)
                } else {
                    allAreas.addAll(it.data)
                    filtersRegionLiveData.postValue(FiltersRegionsState.Start)
                    showArea()
                }
            }
    }

    fun showArea() = viewModelScope.launch(Dispatchers.Default) {
        val content = mutableListOf<RegionDataItem>()
        allAreas.forEach {
            content.addAll(createEntries(it, it))
        }
        content.sortBy { it.currentRegion.name }
        filtersRegionLiveData.postValue(FiltersRegionsState.Content(sortedArea(content)))
    }

    private fun createEntries(parentArea: Area, rootArea: Area): MutableList<RegionDataItem> {
        val content = mutableListOf<RegionDataItem>()
        parentArea.areas.forEach { area ->
            content.add(RegionDataItem(rootRegion = rootArea, currentRegion = area))
            content.addAll(createEntries(area, rootArea))
        }
        return content
    }

    fun findArea(text: String) = viewModelScope.launch(Dispatchers.Default) {
        if (text.isEmpty()) {
            filtersRegionLiveData.postValue(FiltersRegionsState.Content(listOf()))
        } else {
            val content = mutableListOf<RegionDataItem>()
            allAreas.forEach {
                content.addAll(findSecondArea(text.trim().uppercase(), it, it))
            }
            filtersRegionLiveData.postValue(FiltersRegionsState.Content(sortedArea(content)))
            if (content.isEmpty()) {
                filtersRegionLiveData.postValue(FiltersRegionsState.Empty)
            }
        }
    }

    private fun findSecondArea(text: String, parentArea: Area, rootArea: Area): MutableList<RegionDataItem> {
        val content = mutableListOf<RegionDataItem>()
        parentArea.areas.forEach { area ->
            if (area.name.trim().uppercase().indexOf(text) >= 0) {
                content.add(RegionDataItem(rootRegion = rootArea, currentRegion = area))
            }
            content.addAll(findSecondArea(text, area, rootArea))
        }
        return content
    }

    private fun sortedArea(list: List<RegionDataItem>): List<RegionDataItem> {
        val yoRule = "& а < б < в < г < д < е < ё < ж < з < и < й < к < л < м < н < о < п < р < с " +
            "< т < у < ф < х < ц < ч < ш < щ < ъ < ы < ь < я < ю < я"
        val ruleBasedCollator = RuleBasedCollator(yoRule)
        return list.sortedWith { a, b ->
            ruleBasedCollator.compare(a.currentRegion.name.lowercase(), b.currentRegion.name.lowercase())
        }
    }
}
