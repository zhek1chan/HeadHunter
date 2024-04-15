package ru.practicum.android.diploma.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.practicum.android.diploma.data.filters.FiltersRepository
import ru.practicum.android.diploma.data.filters.FiltersRepositoryImpl
import ru.practicum.android.diploma.data.filters.FiltersStorageRepositoryImpl
import ru.practicum.android.diploma.domain.filters.FiltersInteractor
import ru.practicum.android.diploma.domain.filters.FiltersInteractorImpl

private const val filtersPrefs = "FILTERS_PREFS"

val FiltersModule = module {

    singleOf(::FiltersRepositoryImpl).bind<FiltersRepository>()
    factoryOf(::FiltersInteractorImpl).bind<FiltersInteractor>()

    single(qualifier = named("filtersPrefs")) {
        provideFiltersPreferences(androidApplication(), filtersPrefs)
    }

    single { FiltersStorageRepositoryImpl(get(named("filtersPrefs"))) }
}

private fun provideFiltersPreferences(app: Application, key: String): SharedPreferences =
    app.getSharedPreferences(key, Context.MODE_PRIVATE)
