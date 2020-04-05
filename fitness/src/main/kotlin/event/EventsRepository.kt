package event

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

class EventsRepository : EventRead, EventWrite {
    private val events = LinkedBlockingQueue<UserEvent>()
    private val counter = AtomicLong()

    override fun getEvents(uid: Long): Sequence<UserEvent> =
        events.asSequence().filter { it.uid == uid }

    override fun saveEvent(event: UserEvent): Unit =
        events.put(event)

    override fun getNextUid(): Long = counter.incrementAndGet()
}

