package users.http

interface IExchangeHttpClient {
    suspend fun getSharePrice(name: String): Int?
    suspend fun buyShares(id: Int, name: String, cnt: Int)
    suspend fun sellShares(id: Int, name: String, cnt: Int)
}
