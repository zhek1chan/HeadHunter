package ru.practicum.android.diploma.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import ru.practicum.android.diploma.common.Constants
import ru.practicum.android.diploma.data.dto.Response

class RetrofitNetworkClient(
    private val context: Context,
    private val hhService: HhApi
) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        try {
            if (!isConnected()) {
                return Response().apply { resultCode = Constants.NO_CONNECTION }
            }
            return Response().apply { resultCode = Constants.BAD_REQUEST }
        } catch (e: Exception) {
            return Response().apply { resultCode = Constants.BAD_REQUEST }
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
