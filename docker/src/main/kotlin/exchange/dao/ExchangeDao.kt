package exchange.dao

class ExchangeDao : IExchangeDao {

    val shares = mutableMapOf<String, Pair<Int, Int>>()

    override fun addExistingCompany(name: String, cnt: Int) {
        if (cnt <= 0) {
            throw Exception("count must be positive")
        }
        val value = shares[name]
            ?: throw Exception("there is no company $name in list")
        shares[name] = Pair(value.first + cnt, value.second)
    }

    override fun addCompany(name: String, cnt: Int, price: Int) {
        if (cnt <= 0) {
            throw Exception("count must be positive")
        }
        if (price <= 0) {
            throw Exception("price must be positive")
        }
        if (doesShareExist(name)) {
            throw Exception("company $name exists in list")
        }
        shares[name] = Pair(cnt, price)
    }

    override fun getShares(name: String): Pair<String, Pair<Int, Int>> {
        val value = shares[name]
            ?: throw Exception("there is no company $name in list")
        return Pair(name, value)
    }

    override fun buyShare(name: String, cnt: Int) {
        if (cnt <= 0) {
            throw Exception("count must be positive")
        }
        val value = shares[name]
            ?: throw Exception("there is no company $name in list")
        if (value.first - cnt < 0) {
            throw Exception("there are not enough shares on exchange")
        }
        shares[name] = Pair(value.first - cnt, value.second)
    }

    override fun sellShare(name: String, cnt: Int) {
        if (cnt <= 0) {
            throw Exception("count must be positive")
        }
        val value = shares[name]
            ?: throw Exception("there is no company $name in list")
        shares[name] = Pair(value.first + cnt, value.second)
    }

    override fun changePrice(name: String, change: Int) {
        val value = shares[name]
            ?: throw Exception("there is no company $name in list")
        val newPrice = value.second + change
        if (newPrice <= 0) {
            throw Exception("price must be positive")
        }
        shares[name] = Pair(value.first, newPrice)
    }

    private fun doesShareExist(name: String) = shares.containsKey(name)
}
