package exchange

import exchange.dao.ExchangeDao
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlin.Exception

fun main() {
    runBlocking {
        val dao = ExchangeDao()
        val server = embeddedServer(Netty, port = 2400) {
            routing {
                get("/add-company") {
                    val name = call.request.queryParameters["name"]
                    val cnt = call.request.queryParameters["count"]
                    val price = call.request.queryParameters["price"]
                    if (name == null || cnt == null || price == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        dao.addCompany(name, cnt.toInt(), price.toInt())
                        call.respondText("$name added")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
                get("/add-existing-company") {
                    val name = call.request.queryParameters["name"]
                    val cnt = call.request.queryParameters["count"]
                    if (name == null || cnt == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        dao.addExistingCompany(name, cnt.toInt())
                        call.respondText("Added new shares to company $name")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
                get("/get-price") {
                    val name = call.request.queryParameters["name"]
                    if (name == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else {
                        val (share, price) = dao.getShares(name)
                        call.respondText("${price.second}")
                    }
                }
                get("/buy-share") {
                    val name = call.request.queryParameters["name"]
                    val cnt = call.request.queryParameters["count"]
                    if (name == null || cnt == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        val pr = dao.buyShare(name, cnt.toInt())
                        call.respondText("$pr")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
                get("/sell-share") {
                    val name = call.request.queryParameters["name"]
                    val cnt = call.request.queryParameters["count"]
                    if (name == null || cnt == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        val pr = dao.sellShare(name, cnt.toInt())
                        call.respondText("$pr")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
                get("/change-price") {
                    val name = call.request.queryParameters["name"]
                    val change = call.request.queryParameters["change"]
                    if (name == null || change == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        dao.changePrice(name, change.toInt())
                        call.respondText("Price changed")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
            }
        }
        server.start(wait = true)
    }
}