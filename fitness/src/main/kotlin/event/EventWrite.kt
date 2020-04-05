package event

interface EventWrite : EventRead {
    fun saveEvent(event: UserEvent)
    fun getNextUid(): Long

    fun <T : UserEvent> saving(producer: () -> T): T {
        val event = producer()
        saveEvent(event)
        return event
    }

    fun <T : UserEvent> creating(producer: (Long) -> T): T =
        saving { producer(getNextUid()) }
}
