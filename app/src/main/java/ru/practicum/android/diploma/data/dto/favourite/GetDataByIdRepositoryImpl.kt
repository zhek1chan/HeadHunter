package ru.practicum.android.diploma.data.dto.favourite

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.data.dto.room.VacancyDetails
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.dto.room.database.AppDatabase
import ru.practicum.android.diploma.domain.favorite.GetDataByIdRepository

class GetDataByIdRepositoryImpl(
    private val db: AppDatabase,
    private val networkClient: NetworkClient
) : GetDataByIdRepository {

    /*override fun getById(id: String): Flow<Resource<VacancyDetails>> = flow {
        val vacancyFromDb = db.vacancyDao().getVacancyById(id)?.let { vacancyEntity ->
            VacancyConverter.map(vacancyEntity)
        }
        if (vacancyFromDb != null) {
            val response = networkClient.doRequest(VacancyDetailsSearchRequest(id))
            if (response.resultCode == Constant.SUCCESS_RESULT_CODE) {
                db.vacancyDao().updateVacancy(
                    VacancyConverter.map(
                        VacancyDetailsConverter.map((response as VacancyDetailsSearchResponse).dto)
                    )
                )
                emit(
                    Resource(
                        VacancyDetailsConverter.map(response.dto).apply {
                            isFavorite.isFavorite = true
                        }
                    )
                )
            } else {
                emit(Resource(vacancyFromDb))
            }
        } else {
            val response = networkClient.doRequest(VacancyDetailsSearchRequest(id))
            when (response.resultCode) {
                Constant.NO_CONNECTIVITY_MESSAGE -> Resource(ErrorNetwork.NO_CONNECTIVITY_MESSAGE)

                Constant.SUCCESS_RESULT_CODE -> emit(
                    Resource(VacancyDetailsConverter.map((response as VacancyDetailsSearchResponse).dto))
                )

                else -> Resource(ErrorNetwork.SERVER_ERROR_MESSAGE)
            }
        }
    }.flowOn(Dispatchers.IO)*/
    override fun getById(id: String): Flow<Resource<VacancyDetails>> {
        TODO("Убрать комментирование с функции, когда будет готов поиск")
    }
}
