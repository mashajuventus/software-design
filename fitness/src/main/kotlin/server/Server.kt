package server

import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineContext
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.html.body
import kotlinx.html.p
import system.Manager
import system.Report
import system.Turnstile
import java.time.LocalDateTime

lateinit var manager: Manager
lateinit var turnstile: Turnstile
lateinit var report: Report


fun Application.main() {
    install(CallLogging)
    install(DefaultHeaders)
    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respondText { "Not found" }
        }
    }

    routing {
        get("/manager/registerClient") {
            val name = call.request.queryParameters["name"]
            when (name) {
                null -> call.respondText { "Error: please specify 'name'" }
                else -> {
                    val (uid, _) = manager.createUser(name)
                    call.respondText { "$name uid is $uid" }
                }
            }
        }

        get("/manager/prolongSubscription") {
            val uid = call.request.queryParameters["uid"]
            val until = call.request.queryParameters["until"]
            when {
                uid == null -> call.respondText { "Error: please specify 'uid'" }
                until == null -> call.respondText { "Error: please specify 'until'" }
                else -> try {
                    val event = manager.prolongSubscription(uid.toLong(), LocalDateTime.parse(until))
                    call.respondHtml {
                        body {
                            p {
                                +"User #$uid has prolonged subscription until $until"
                            }
                        }
                    }
                } catch (e: Exception) {
                    call.respondText { "Error during subscription prolonging: ${e.message}" }
                }
            }
        }

        get("/turnstile/checkIn") {
            tryCheck { uid, time ->
                try {
                    turnstile.checkIn(uid = uid, time = time)
                    call.respondText { "User #$uid checked in" }
                } catch (e: Exception) {
                    call.respondText { "CheckIn error: ${e.message}" }
                }
            }
        }

        get("/turnstile/checkOut") {
            tryCheck { uid, time ->
                try {
                    turnstile.checkOut(uid = uid, time = time)
                    call.respondText { "User #$uid checked out" }
                } catch (e: Exception) {
                    call.respondText { "CheckOut error: ${e.message}" }
                }
            }
        }

        get("/report/frequency") {
            try {
                val uid = call.request.queryParameters["uid"]
                val fromReq = call.request.queryParameters["time"]
                val from = if (fromReq == null) LocalDateTime.MIN else LocalDateTime.parse(fromReq)
                val toReq = call.request.queryParameters["time"]
                val to = if (toReq == null) LocalDateTime.MAX else LocalDateTime.parse(toReq)
                when (uid) {
                    null -> call.respondText { "Error: please specify 'uid'" }
                    else -> {
                        val averageVisits = report.averageVisitsPerWeek(uid = uid.toLong(), from = from, to = to)
                        call.respondText { "User #$uid visits $averageVisits times a week" }
                    }
                }
            } catch (e: Exception) {
                call.respondText { "Error during report creation: ${e.message}" }
            }
        }

        get("/report/averageSession") {
            try {
                val uid = call.request.queryParameters["uid"]
                when (uid) {
                    null -> call.respondText { "Error: please specify 'uid'" }
                    else -> {
                        val averageDuration = report.averageSession(uid = uid.toLong())
                        call.respondText { "Average duration of client #$uid is $averageDuration" }
                    }
                }
            } catch (e: Exception) {
                call.respondText { "Summary error: ${e.message}" }
            }
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.tryCheck(
    action: suspend PipelineContext<Unit, ApplicationCall>.(Long, LocalDateTime) -> Unit
) {
    val uid = call.request.queryParameters["uid"]
    val timeParameter = call.request.queryParameters["time"]
    val time = if (timeParameter == null) LocalDateTime.now() else LocalDateTime.parse(timeParameter)
    when (uid) {
        null -> call.respondText { "Error: please specify 'uid'" }
        else -> action(uid.toLong(), time)
    }
}