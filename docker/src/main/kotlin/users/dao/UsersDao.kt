package users.dao

import users.http.IExchangeHttpClient
import users.user.User
import kotlin.Exception

class UsersDao(private val client: IExchangeHttpClient) : IUsersDao {
    var users = mutableListOf<User>()

    override suspend fun getMoney(id: Int): Int {
        if (!isUserExist(id)) {
            throw Exception("there is no user with id = $id")
        }
        var ans = users[id].balance
        for (value in getShares(id).values) {
            ans += value.first * value.second
        }
        return ans
    }

    override fun getBalance(id: Int): Int {
        if (!isUserExist(id)) {
            throw Exception("there is no user with id = $id")
        }
        return users[id].balance
    }

    private suspend fun getShares(id: Int): Map<String, Pair<Int, Int>> {
        if (!isUserExist(id)) {
            throw Exception("there is no user with id = $id")
        }
        val pricedShares = mutableMapOf<String, Pair<Int, Int>>()
        for ((name, cnt) in users[id].shares) {
            val price = client.getSharePrice(name)!!.toInt()
            pricedShares[name] = Pair(cnt, price)
        }
        return pricedShares.toMap()
    }

    override fun addUser(name: String): Int {
        val id = users.size
        val user = User(id, name, 0, mutableMapOf())
        users.add(user)
        return id
    }

    override fun addMoney(id: Int, change: Int) {
        if (!isUserExist(id)) {
            throw Exception("there is no user with id = $id")
        }
        if (change <= 0) {
            throw Exception("change must be positive")
        }
        users[id].changeMoney(change)
    }

    override fun removeMoney(id: Int, change: Int) {
        if (!isUserExist(id)) {
            throw Exception("there is no user with id = $id")
        }
        if (change <= 0) {
            throw Exception("change must be positive")
        }
        users[id].changeMoney(-change)
    }

    override suspend fun buyShare(id: Int, companyName: String, cnt: Int) {
        if (!isUserExist(id)) {
            throw Exception("there is no user with id = $id")
        }
        if (cnt <= 0) {
            throw Exception("count must be positive")
        }
        client.buyShares(id, companyName, cnt)
        val price = client.getSharePrice(companyName)
            ?: throw ArithmeticException("no price from client")
        users[id].addShare(companyName, cnt, price)
    }

    override suspend fun sellShare(id: Int, companyName: String, cnt: Int) {
        if (!isUserExist(id)) {
            throw Exception("there is no user with id = $id")
        }
        if (cnt <= 0) {
            throw Exception("count must be positive")
        }
        client.sellShares(id, companyName, cnt)
        val price = client.getSharePrice(companyName)
            ?: throw Exception("no price from client")
        users[id].addShare(companyName, -cnt, price)
    }

    private fun isUserExist(id: Int) = id >= 0 && id < users.size
}
