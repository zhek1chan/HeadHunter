package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.data.dto.favourite.CheckVacancyOnLikeRepositoryImpl
import ru.practicum.android.diploma.data.dto.favourite.DeleteVacancyRepositoryImpl
import ru.practicum.android.diploma.data.dto.favourite.GetVacancyRepositoryImpl
import ru.practicum.android.diploma.data.dto.favourite.SaveVacancyRepositoryImpl
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.domain.favorite.CheckVacancyOnLikeRepository
import ru.practicum.android.diploma.domain.favorite.DeleteVacancyRepository
import ru.practicum.android.diploma.domain.favorite.GetVacancyRepository
import ru.practicum.android.diploma.domain.favorite.SaveVacancyRepository
import ru.practicum.android.diploma.presentation.favourite.viewmodel.FavoriteViewModel

val FavouriteModule = module {
    viewModel {
        FavoriteViewModel(
            get(),
            get()
        )
    }
    single<DeleteVacancyRepository> {
        DeleteVacancyRepositoryImpl(get())
    }
    single<GetVacancyRepository> {
        GetVacancyRepositoryImpl(get())
    }
    single<SaveVacancyRepository> {
        SaveVacancyRepositoryImpl(get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }
    single<CheckVacancyOnLikeRepository> {
        CheckVacancyOnLikeRepositoryImpl(get())
    }
}
