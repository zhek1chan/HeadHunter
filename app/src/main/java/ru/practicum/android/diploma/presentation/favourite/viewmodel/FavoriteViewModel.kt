package ru.practicum.android.diploma.presentation.favourite.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.favorite.DeleteVacancyInteractor
import ru.practicum.android.diploma.domain.favorite.DeleteVacancyRepository
import ru.practicum.android.diploma.domain.favorite.GetVacancyInteractor
import ru.practicum.android.diploma.domain.favorite.GetVacancyRepository
import ru.practicum.android.diploma.presentation.favourite.FavouritesState
import java.sql.SQLException

class FavoriteViewModel(
    private val getVacs: GetVacancyInteractor,
    private val deleteVacancy: DeleteVacancyInteractor,
) : ViewModel() {

    private val screenStatement: MutableLiveData<FavouritesState> = MutableLiveData()
    val screenState: LiveData<FavouritesState> get() = screenStatement

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getVacs.getVacancy().collect { list ->
                    when {
                        list == null -> screenStatement.postValue(FavouritesState.DbError)
                        list.isEmpty() -> screenStatement.postValue(FavouritesState.Empty)
                        else -> {
                            screenStatement.postValue(FavouritesState.Content(list))
                            Log.d("got values from db", "$list")
                        }
                    }
                }
            } catch (e: SQLException) {
                screenStatement.postValue(FavouritesState.DbError)
                Log.e("Tag", e.stackTraceToString())
            }
        }
    }

    fun deleteVacancyFromFavorite(vacancyId: String) {
        viewModelScope.launch {
            deleteVacancy.deleteVacancy(vacancyId)
        }
    }
}
