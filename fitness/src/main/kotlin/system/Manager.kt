package system

import event.EventWrite
import event.SubscriptionProlonged
import event.UserCreated
import java.time.LocalDateTime

class Manager(private val repository: EventWrite) {
    fun createUser(name: String): UserCreated =
        repository.creating { UserCreated(it, name) }

    fun prolongSubscription(uid: Long, until: LocalDateTime): SubscriptionProlonged {
        require(repository.subscribedUntil(uid) < until) { "Client is already subscribed for longer period" }
        return repository.saving { SubscriptionProlonged(uid, until) }
    }
}