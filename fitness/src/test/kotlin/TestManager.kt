import event.EventsRepository
import event.UserCreated
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import system.Manager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class TestManager {
    private lateinit var executor: ExecutorService

    private lateinit var repository: EventsRepository
    private lateinit var manager: Manager

    @BeforeEach
    fun setup() {
        executor = Executors.newFixedThreadPool(16)!!
        repository = EventsRepository()
        manager = Manager(repository)
    }

    @Test
    fun createUserTest() {
        val times = 10
        val uidSet = mutableSetOf<Long>()
        val futures = Array(times) {
            executor.submit {
                val name = "NewUser#$it"
                val event = manager.createUser(name)
                Assertions.assertFalse(uidSet.contains(event.uid))
                uidSet.add(event.uid)
                Assertions.assertEquals(name, event.name)
            }
        }
        executor.shutdown()
        futures.forEach { it.get() }
        for (uid in uidSet) {
            val events = repository.getEvents(uid).toList()
            Assertions.assertEquals(1, events.size)
            Assertions.assertTrue(events[0] is UserCreated)
            Assertions.assertEquals(uid, (events[0] as UserCreated).uid)
        }
    }

    @AfterEach
    fun teardown() {
        executor.shutdownNow()
        executor.awaitTermination(2, TimeUnit.SECONDS)
    }
}