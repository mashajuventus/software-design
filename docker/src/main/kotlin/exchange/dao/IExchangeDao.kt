package exchange.dao

interface IExchangeDao {
    fun addCompany(name: String, cnt: Int, price: Int)
    fun addExistingCompany(name: String, cnt: Int)
    fun getShares(name: String): Pair<String, Pair<Int, Int>>
    fun buyShare(name: String, cnt: Int)
    fun sellShare(name: String, cnt: Int)
    fun changePrice(name: String, change: Int)
}
