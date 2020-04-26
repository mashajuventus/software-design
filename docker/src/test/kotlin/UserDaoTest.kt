import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.testcontainers.containers.FixedHostPortGenericContainer
import users.dao.UsersDao
import users.http.ExchangeHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class UserDaoTest {
    private val hostPort = 9050
    private val containerPort = 2400

    private class KFixedHostPortGenericContainer(imageName: String) :
        FixedHostPortGenericContainer<KFixedHostPortGenericContainer>(imageName)

    private var exchange: KFixedHostPortGenericContainer? = null
    private val client = ExchangeHttpClient(hostPort)

    val receiver = HttpClient {
        expectSuccess = false
    }

    private var dao = UsersDao(client)

    @Before
    fun startExchange() {
        dao = UsersDao(client)
        exchange = KFixedHostPortGenericContainer("maruss/exchange:latest")
            .withFixedExposedPort(hostPort, containerPort)
            .withExposedPorts(containerPort)
        exchange!!.start()
    }

    @After
    fun stopExchange() {
        exchange!!.stop()
    }

    @Test
    fun testAddUser() = runBlocking {
        val id = dao.addUser("user0")
        assertEquals(0, dao.getMoney(id))
    }

    @Test
    fun testAddMoneyBalance() = runBlocking {
        val id = dao.addUser("user1")
        assertEquals(0, dao.getMoney(id))
        dao.addMoney(id, 200)
        assertEquals(200, dao.getMoney(id))
        assertEquals(200, dao.getBalance(id))
    }

    @Test(expected = Exception::class)
    fun testSellNonBuyingShares() = runBlocking {
        val id = dao.addUser("user2")
        dao.sellShare(id, "asus", 10)
    }

    @Test(expected = Exception::class)
    fun testBuyFromNonExistingCompany() = runBlocking {
        val id = dao.addUser("user3")
        dao.buyShare(id, "asus", 2)
    }

    @Test
    fun testChangingPrice() = runBlocking {
        val companyName = "asus"

        val request = "http://localhost:$hostPort/add-company?name=$companyName&count=5&price=100"
        val response = receiver.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        assertEquals(100, client.getSharePrice(companyName))

        val changeRequest = "http://localhost:$hostPort/change-price?name=$companyName&change=200"
        val changeResponse = receiver.get<HttpResponse>(changeRequest)
        assertEquals(HttpStatusCode.OK, changeResponse.status)

        assertEquals(300, client.getSharePrice(companyName))
    }

    @Test
    fun testBuyAndSell() = runBlocking {
        val userName = "user4"
        val companyName = "asus"
        val id = dao.addUser(userName)
        dao.addMoney(id, 1000)

        val request = "http://localhost:$hostPort/add-company?name=$companyName&count=5&price=100"
        val response = receiver.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        dao.buyShare(id, companyName, 3)
        assertEquals(700, dao.getBalance(id))
        assertEquals(1000, dao.getMoney(id))

        dao.sellShare(id, companyName, 1)
        assertEquals(800, dao.getBalance(id))
        assertEquals(1000, dao.getMoney(id))
    }

    @Test(expected = Exception::class)
    fun testBuyTooManyShares() = runBlocking {
        val userName = "user5"
        val companyName = "asus"
        val id = dao.addUser(userName)
        dao.addMoney(id, 1000)

        val request = "http://localhost:$hostPort/add-company?name=$companyName&count=5&price=100"
        val response = receiver.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        dao.buyShare(id, companyName, 10)
    }
}
