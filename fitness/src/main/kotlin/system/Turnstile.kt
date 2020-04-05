package system

import event.EventWrite
import event.UserCheckIned
import event.UserCheckOuted
import java.time.LocalDateTime

class Turnstile(private val repository: EventWrite) {
    fun checkIn(uid: Long, time: LocalDateTime = LocalDateTime.now()): UserCheckIned {
        require(time < repository.subscribedUntil(uid)) { "Subscription is expired" }
        require(!repository.isInside(uid)) { "Client has checked in" }
        return repository.saving { UserCheckIned(uid, time) }
    }

    fun checkOut(uid: Long, time: LocalDateTime = LocalDateTime.now()): UserCheckOuted {
        require(repository.isInside(uid)) { "Client has checked out" }
        return repository.saving { UserCheckOuted(uid, time) }
    }
}