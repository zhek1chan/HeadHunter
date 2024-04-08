package ru.practicum.android.diploma.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.DisconnectCause
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.data.Constant
import ru.practicum.android.diploma.data.dto.Response

class RetrofitNetworkClient(
    private val service: HhApi,
    private val context: Context
) : NetworkClient {

    override suspend fun search(dto: JobSearchRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = Constant.NO_CONNECTIVITY_MESSAGE }
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = service.jobSearch(dto.request)
                response.apply { resultCode = Constant.SUCCESS_RESULT_CODE }
            } catch (e: Throwable) {
                e.printStackTrace()
                Response().apply { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    resultCode = DisconnectCause.SERVER_ERROR
                }
                }
            }
        }
    }

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = Constant.NO_CONNECTIVITY_MESSAGE }
        }
        var response = Response()
        return try {
            when (dto) {
                is DetailVacancyRequest -> withContext(Dispatchers.IO) {
                    response = service.getDetailVacancy(vacancyId = dto.id)
                    response.apply { resultCode = Constant.SUCCESS_RESULT_CODE }
                }

                is CountriesRequest -> withContext(Dispatchers.IO) {
                    val result = service.filterCountry()
                    response.apply {
                        countriesList = result
                        resultCode = Constant.SUCCESS_RESULT_CODE
                    }
                }

                is RegionsRequest -> withContext(Dispatchers.IO) {
                    val result = if (dto.idArea.isNullOrEmpty()) {
                        service.filterRegions()
                    } else {
                        listOf(service.filterRegion(dto.idArea))
                    }
                    response.apply {
                        regionsList = result
                        resultCode = Constant.SUCCESS_RESULT_CODE
                    }
                }

                is IndustriesRequest -> withContext(Dispatchers.IO) {
                    val result = service.filterIndustry()
                    response.apply {
                        industriesList = result
                        resultCode = Constant.SUCCESS_RESULT_CODE
                    }
                }

                else -> {
                    response.apply { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        resultCode = DisconnectCause.SERVER_ERROR
                    }
                    }
                }
            }
        } catch (exception: HttpException) {
            Response().apply { resultCode = exception.code() }

        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

}
