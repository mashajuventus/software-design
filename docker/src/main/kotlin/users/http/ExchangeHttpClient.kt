package users.http

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode

class ExchangeHttpClient(port: Int) : IExchangeHttpClient {

    override suspend fun getSharePrice(name: String): Int? {
        val request = "$host/get-price?name=$name"
        val response = client.get<HttpResponse>(request)

        return if (response.status == HttpStatusCode.OK) {
            response.readText().toInt()
        } else {
            null
        }
    }

    override suspend fun buyShares(id: Int, name: String, cnt: Int) {
        val request = "$host/buy-share?id=$id&name=$name&count=$cnt"
        val response = client.get<HttpResponse>(request)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("status is not OK")
        }
    }

    override suspend fun sellShares(id: Int, name: String, cnt: Int) {
        val request = "$host/sell-share?id=$id&name=$name&count=$cnt"
        val response = client.get<HttpResponse>(request)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("status is not OK")
        }
    }

    private val host = "http://localhost:$port"

    val client = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
        }
        expectSuccess = false
    }
}
