package users.dao

interface IUsersDao {
    fun addUser(name: String): Int
    fun addMoney(id: Int, change: Int)
    fun removeMoney(id: Int, change: Int)
    fun getBalance(id: Int): Int
    suspend fun getMoney(id: Int): Int
    suspend fun buyShare(id: Int, companyName: String, cnt: Int)
    suspend fun sellShare(id: Int, companyName: String, cnt: Int)
}
