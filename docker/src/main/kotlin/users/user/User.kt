package users.user

import java.lang.Exception

class User(val id: Int, val name: String, var balance: Int, var shares: MutableMap<String, Int>) {
    // shares = map: CompanyName -> Count

    fun changeMoney(change: Int) {
        if (balance + change < 0) {
            throw Exception("balance isn't enough")
        }
        balance += change
    }

    fun addShare(name: String, cnt: Int, price: Int) {
        if (!canBuy(price, cnt)) {
            throw Exception("too much shares or high price")
        }
        val curCnt = shares.getOrDefault(name, 0)
        if (curCnt + cnt < 0) {
            throw Exception("too much shares to sell")
        }
        shares[name] = curCnt + cnt
        changeMoney(-price * cnt)
    }

    private fun canBuy(price: Int, cnt : Int) = balance - price * cnt >= 0
}

