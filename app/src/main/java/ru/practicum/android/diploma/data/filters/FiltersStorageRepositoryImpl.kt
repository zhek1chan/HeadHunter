package ru.practicum.android.diploma.data.filters

import android.content.SharedPreferences
import ru.practicum.android.diploma.domain.models.Filters

class FiltersStorageRepositoryImpl(private val shPref: SharedPreferences) : FiltersStorageRepository {

    companion object {
        const val FILTERS_COUNTRY = "FILTERS_COUNTRY"
        const val FILTERS_COUNTRY_ID = "FILTERS_COUNTRY_ID"
        const val FILTERS_REGION = "FILTERS_REGION"
        const val FILTERS_REGION_ID = "FILTERS_REGION_ID"
        const val FILTERS_INDUSTRY = "FILTERS_INDUSTRY"
        const val FILTERS_INDUSTRY_ID = "FILTERS_INDUSTRY_ID"
        const val FILTERS_SALARY = "FILTERS_SALARY"
        const val FILTERS_SALARY_ONLY = "FILTERS_SALARY_ONLY"
    }

    override fun getPrefs(): Filters {
        return Filters(
            shPref.getString(FILTERS_COUNTRY, "") ?: "",
            shPref.getString(FILTERS_COUNTRY_ID, "") ?: "",
            shPref.getString(FILTERS_REGION, "") ?: "",
            shPref.getString(FILTERS_REGION_ID, "") ?: "",
            shPref.getString(FILTERS_INDUSTRY, "") ?: "",
            shPref.getString(FILTERS_INDUSTRY_ID, "") ?: "",
            shPref.getString(FILTERS_SALARY, "") ?: "",
            shPref.getBoolean(FILTERS_SALARY_ONLY, false),
        )
    }

    override fun savePrefs(settings: Filters) {
        shPref.edit().putString(FILTERS_COUNTRY, settings.country).apply()
        shPref.edit().putString(FILTERS_COUNTRY_ID, settings.countryId).apply()
        shPref.edit().putString(FILTERS_REGION, settings.region).apply()
        shPref.edit().putString(FILTERS_REGION_ID, settings.regionId).apply()
        shPref.edit().putString(FILTERS_INDUSTRY, settings.industry).apply()
        shPref.edit().putString(FILTERS_INDUSTRY_ID, settings.industryId).apply()
        shPref.edit().putString(FILTERS_SALARY, settings.expectedSalary).apply()
        shPref.edit().putBoolean(FILTERS_SALARY_ONLY, settings.salaryOnlyCheckbox).apply()
    }

    override fun clearPrefs() {
        shPref.edit().remove(FILTERS_COUNTRY).apply()
        shPref.edit().remove(FILTERS_COUNTRY_ID).apply()
        shPref.edit().remove(FILTERS_REGION).apply()
        shPref.edit().remove(FILTERS_REGION_ID).apply()
        shPref.edit().remove(FILTERS_INDUSTRY).apply()
        shPref.edit().remove(FILTERS_INDUSTRY_ID).apply()
        shPref.edit().remove(FILTERS_SALARY).apply()
        shPref.edit().remove(FILTERS_SALARY_ONLY).apply()
    }
}
