package event

import java.time.LocalDateTime

interface EventRead {
    fun getEvents(uid: Long): Sequence<UserEvent>

    fun subscribedUntil(uid: Long): LocalDateTime = (getEvents(uid).lastOrNull { it is SubscriptionProlonged } as SubscriptionProlonged?)?.until ?: LocalDateTime.MIN

    fun isInside(uid: Long): Boolean {
        var inside = false
        for (event in getEvents(uid)) when (event) {
            is UserCheckIned  -> {
                assert(!inside)
                inside = true
            }
            is UserCheckOuted -> {
                assert(inside)
                inside = false
            }
        }
        return inside
    }
}
