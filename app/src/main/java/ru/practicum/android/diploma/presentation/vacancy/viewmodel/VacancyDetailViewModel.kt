package ru.practicum.android.diploma.presentation.vacancy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.data.converters.DetailsConverter
import ru.practicum.android.diploma.domain.favorite.CheckVacancyOnLikeInteractor
import ru.practicum.android.diploma.domain.favorite.DeleteVacancyInteractor
import ru.practicum.android.diploma.domain.favorite.ExternalNavigator
import ru.practicum.android.diploma.domain.favorite.SaveVacancyInteractor
import ru.practicum.android.diploma.domain.models.DetailVacancy
import ru.practicum.android.diploma.domain.search.VacancyInteractor
import ru.practicum.android.diploma.presentation.vacancy.state.VacancyState

class VacancyDetailViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val deleteVacancyInteractor: DeleteVacancyInteractor,
    private val saveVacancyInteractor: SaveVacancyInteractor,
    private val convertor: DetailsConverter,
    private val likeInteractor: CheckVacancyOnLikeInteractor,
    private val externalNavigator: ExternalNavigator
) : ViewModel() {

    private val _vacancyState = MutableLiveData<VacancyState>()
    val vacancyState: LiveData<VacancyState> = _vacancyState
    private var likeIndicator = MutableLiveData<Boolean>()
    private var likeJob: Job? = null

    private fun renderState(state: VacancyState) {
        _vacancyState.postValue(state)
    }

    fun getVacancyDetail(id: String) {
        if (id.isNotEmpty()) {
            renderState(VacancyState.Loading)
            viewModelScope.launch {
                vacancyInteractor.getDetailVacancy(id).collect { resource ->
                    processResult(resource)
                }
            }
        }
    }

    private fun processResult(resource: Resource<DetailVacancy>) {
        if (resource.data != null) {
            renderState(VacancyState.Content(resource.data))
        } else {
            renderState(VacancyState.Error)
        }
    }

    fun clickOnLike() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
        if (vacancyState.value is VacancyState.Content) {
            if ((vacancyState.value as VacancyState.Content).vacancy.isFavorite.isFavorite) {
                _vacancyState.postValue((_vacancyState.value as VacancyState.Content).apply {
                    vacancy.isFavorite.isFavorite = false
                })
                viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                    deleteVacancyInteractor.deleteVacancy(
                        (vacancyState.value as VacancyState.Content).vacancy.id
                    )
                    Log.d("VacancyVM", "Vacancy was deleted from favs")
                }
            } else {
                _vacancyState.postValue((_vacancyState.value as VacancyState.Content).apply {
                    vacancy.isFavorite.isFavorite = true
                })
                viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                    saveVacancyInteractor.saveVacancy(
                        convertor.map((vacancyState.value as VacancyState.Content).vacancy)
                    )
                }
            }
        }
    }

    fun clickOnLikeWithDb() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
        if (vacancyState.value is VacancyState.ContentFromDb) {
            if (
                (vacancyState.value as VacancyState.ContentFromDb)
                    .vacancy
                    .isFavorite
                    .isFavorite
            ) {
                _vacancyState.postValue(
                    (_vacancyState.value as VacancyState.ContentFromDb).apply {
                        vacancy.isFavorite.isFavorite = false
                    }
                )
                viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                    deleteVacancyInteractor.deleteVacancy(
                        (vacancyState.value as VacancyState.ContentFromDb)
                            .vacancy.id
                    )
                    Log.d("VacancyVM", "Vacancy was deleted from favs")
                }
            } else {
                _vacancyState.postValue(
                    (_vacancyState.value as VacancyState.ContentFromDb).apply {
                        vacancy.isFavorite.isFavorite = true
                    }
                )
                viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                    saveVacancyInteractor.saveVacancy(
                        convertor.map((vacancyState.value as VacancyState.ContentFromDb).vacancy)
                    )
                }
            }
        }
    }

    fun onLikedCheck(v: String): LiveData<Boolean> {
        likeJob = viewModelScope.launch {
            while (true) {
                delay(BUTTON_PRESSING_DELAY)
                v.let { id ->
                    likeInteractor.favouritesCheck(id).collect { value ->
                        likeIndicator.postValue(value)
                    }
                }
            }
        }
        return likeIndicator
    }

    fun checkBeforeRender(id: String): Boolean {
        return likeInteractor.checkOnFavDB(id)
    }

    fun getVacancyFromDb(id: String) {
        _vacancyState.postValue(VacancyState.ContentFromDb(likeInteractor.getVacancy(id)))
    }

    fun shareVacancy() {
        if (vacancyState.value is VacancyState.Content) {
            val screenState = vacancyState.value as VacancyState.Content
            externalNavigator.share("https://hh.ru/vacancy/${screenState.vacancy.id}")
        }
    }

    companion object {
        const val BUTTON_PRESSING_DELAY = 300L
    }
}
