package system

import event.EventsRepository
import event.UserCheckIned
import event.UserCheckOuted
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.max

class Report(private val repository: EventsRepository) {
    fun averageSession(uid: Long): Duration {
        var cnt = 0L
        var duration = Duration.ZERO
        var prevCheckIn: LocalDateTime? = null
        for (event in repository.getEvents(uid)) when (event) {
            is UserCheckIned -> {
                cnt++
                prevCheckIn = event.time
            }
            is UserCheckOuted -> {
                requireNotNull(prevCheckIn)
                duration += Duration.between(prevCheckIn, event.time)
            }
        }
        return if (cnt == 0L) duration else duration.dividedBy(cnt)
    }

    fun averageVisitsPerWeek(uid: Long, from: LocalDateTime = LocalDateTime.MIN, to: LocalDateTime = LocalDateTime.MAX): Double {
        var cnt = 0L
        var firstVisit: LocalDateTime? = null
        var lastVisit: LocalDateTime? = null
        for (event in repository.getEvents(uid)) {
            if (event is UserCheckIned && event.time in from..to) {
                cnt++
                firstVisit = firstVisit ?: event.time
                lastVisit = event.time
            }
        }
        val activeDays = if (firstVisit == null) 1L else max(1, Duration.between(firstVisit, lastVisit).toDays())
        return cnt / activeDays.toDouble()
    }
}