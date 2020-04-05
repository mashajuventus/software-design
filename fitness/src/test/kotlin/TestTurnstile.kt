import event.EventsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import system.Manager
import system.Turnstile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.util.*

class TestTurnstile {
    private val random = Random(100)

    private lateinit var repository: EventsRepository
    private lateinit var manager: Manager
    private lateinit var turnstile: Turnstile

    private lateinit var startTime: LocalDateTime
    private lateinit var clients: MutableList<Client>
    private lateinit var clientsInside: Set<Long>

    @BeforeEach
    fun setUp() {
        repository = EventsRepository()
        manager = Manager(repository)
        turnstile = Turnstile(repository)
        startTime = LocalDateTime.of(
            LocalDate.of(2020, Month.MARCH, 1),
            LocalTime.of(11, 0)
        )
        clients = mutableListOf()
        repeat(5) { newClient(startTime.plusMonths(random.nextInt(12) + 1L)) }
        clientsInside = mutableSetOf()
    }

    @Test
    fun oneYearTest() {
        val finishTime = startTime.plusYears(1)
        var currentTime = startTime
        while (currentTime < finishTime) {
            val p = random.nextDouble()
            when {
                p < 0.05 -> newClient(currentTime)
                p < 0.1 -> prolongSubscription()
                p < 0.6 -> checkIn(currentTime)
                else -> checkOut(currentTime)
            }
            currentTime = currentTime.plusMinutes(random.nextInt(50) + 1L)
        }
    }

    private fun checkIn(time: LocalDateTime) {
        val ind = random.nextInt(clients.size)
        val (uid, until, insideStatus) = clients[ind]
        if (insideStatus || time >= until) {
            assertThrows<IllegalArgumentException> { turnstile.checkIn(uid, time) }
        } else {
            val event = turnstile.checkIn(uid, time)
            assertEquals(uid, event.uid)
            assertEquals(time, event.time)
            assertTrue(repository.isInside(uid))
            clients[ind] = Client(uid, until, true)
        }
    }

    private fun checkOut(time: LocalDateTime) {
        val ind = random.nextInt(clients.size)
        val (uid, until, insideStatus) = clients[ind]
        if (!insideStatus) {
            assertThrows<IllegalArgumentException> { turnstile.checkOut(uid, time) }
        } else {
            val event = turnstile.checkOut(uid, time)
            assertEquals(uid, event.uid)
            assertEquals(time, event.time)
            assertTrue(!repository.isInside(uid))
            clients[ind] = Client(uid, until, false)
        }
    }

    private fun prolongSubscription() {
        val ind = random.nextInt(clients.size)
        val (uid, expiration, inside) = clients[ind]
        val newExpiration = expiration.plusMonths(random.nextInt(3) + 1L)
        val event = manager.prolongSubscription(uid, newExpiration)
        assertEquals(uid, event.uid)
        assertEquals(newExpiration, event.until)
        assertEquals(newExpiration, repository.subscribedUntil(uid))
        clients[ind] = Client(uid, newExpiration, inside)
    }

    private fun newClient(until: LocalDateTime = startTime.plusMonths(6)): Client {
        val event = manager.createUser("NewUser#${random.nextInt(10)}")
        manager.prolongSubscription(event.uid, until)
        val client = Client(event.uid, until)
        clients.add(client)
        return client
    }

    data class Client(val uid: Long, val until: LocalDateTime, val inside: Boolean = false)
}