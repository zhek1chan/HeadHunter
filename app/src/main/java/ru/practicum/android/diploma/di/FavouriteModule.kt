package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.data.dto.favourite.CheckOnLikeRepositoryImpl
import ru.practicum.android.diploma.data.dto.favourite.DeleteDataRepositoryImpl
import ru.practicum.android.diploma.data.dto.favourite.GetDataByIdRepositoryImpl
import ru.practicum.android.diploma.data.dto.favourite.GetDataRepositoryImpl
import ru.practicum.android.diploma.data.dto.favourite.SaveDataRepositoryImpl
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.domain.favorite.CheckOnLikeRepository
import ru.practicum.android.diploma.domain.favorite.DeleteDataRepository
import ru.practicum.android.diploma.domain.favorite.GetDataByIdRepository
import ru.practicum.android.diploma.domain.favorite.GetDataRepository
import ru.practicum.android.diploma.domain.favorite.SaveDataRepository
import ru.practicum.android.diploma.presentation.favourite.viewmodel.FavoriteViewModel

val FavouriteModule = module {
    viewModel {
        FavoriteViewModel(
            get(),
            get()
        )
    }
    single<DeleteDataRepository> {
        DeleteDataRepositoryImpl(get())
    }
    single<GetDataRepository> {
        GetDataRepositoryImpl(get())
    }
    single<GetDataByIdRepository> {
        GetDataByIdRepositoryImpl(get(), get())
    }
    single<SaveDataRepository> {
        SaveDataRepositoryImpl(get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }
    single<CheckOnLikeRepository> {
        CheckOnLikeRepositoryImpl(get())
    }
}
