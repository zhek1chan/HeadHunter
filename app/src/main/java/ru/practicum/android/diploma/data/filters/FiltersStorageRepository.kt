package ru.practicum.android.diploma.data.filters

import ru.practicum.android.diploma.domain.models.Filters

interface FiltersStorageRepository {

    fun getPrefs(): Filters
    fun savePrefs(settings: Filters)
    fun clearPrefs()

}
