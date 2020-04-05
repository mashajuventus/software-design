package event

import java.time.LocalDateTime

sealed class UserEvent(open val uid: Long)

data class UserCreated(override val uid: Long, val name: String) : UserEvent(uid)

data class UserCheckIned(override val uid: Long, val time: LocalDateTime) : UserEvent(uid)

data class UserCheckOuted(override val uid: Long, val time: LocalDateTime) : UserEvent(uid)

data class SubscriptionProlonged(override val uid: Long, val until: LocalDateTime) : UserEvent(uid)

