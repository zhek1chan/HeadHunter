package ru.practicum.android.diploma.di

import androidx.room.Room
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.converters.DetailsConverter
import ru.practicum.android.diploma.data.converters.VacancyConverter
import ru.practicum.android.diploma.data.converters.VacancyDetailsConverter
import ru.practicum.android.diploma.data.converters.VacancyShortMapper
import ru.practicum.android.diploma.data.dto.room.database.AppDatabase

val DBModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
    factory { VacancyConverter }
    factory { VacancyDetailsConverter }
    factory { DetailsConverter() }
    factory { VacancyShortMapper }

    single { Gson() }
}
