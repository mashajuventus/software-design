package users

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import users.dao.UsersDao
import users.http.ExchangeHttpClient
import io.ktor.routing.get
import io.ktor.routing.routing

fun main() {
    runBlocking {
        val client = ExchangeHttpClient(2400)
        val dao = UsersDao(client)
        val server = embeddedServer(Netty, port = 55161) {
            routing {
                get("/add-user") {
                    val name = call.request.queryParameters["name"]
                    if (name == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else {
                        val id = dao.addUser(name)
                        call.respondText("Added new user with id $id")
                    }
                }
                get("/add-money") {
                    val id = call.request.queryParameters["id"]
                    val change = call.request.queryParameters["change"]
                    if (id == null || change == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        dao.addMoney(id.toInt(), change.toInt())
                        call.respondText("Added $change to $id")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
                get("/get-money") {
                    val id = call.request.queryParameters["id"]
                    if (id == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        val balance = dao.getMoney(id.toInt())
                        call.respondText("$balance")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
                get("/buy-share") {
                    val id = call.request.queryParameters["id"]
                    val name = call.request.queryParameters["name"]
                    val cnt = call.request.queryParameters["count"]
                    if (id == null || name == null || cnt == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        dao.buyShare(id.toInt(), name, cnt.toInt())
                        call.respondText("bought")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
                get("/sell-share") {
                    val id = call.request.queryParameters["id"]
                    val name = call.request.queryParameters["name"]
                    val cnt = call.request.queryParameters["count"]
                    if (id == null || name == null || cnt == null) {
                        call.respondText("Not enough parameters", status = HttpStatusCode.BadRequest)
                    } else try {
                        dao.sellShare(id.toInt(), name, cnt.toInt())
                        call.respondText("sold")
                    } catch (e: Exception) {
                        call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
                    }
                }
            }
        }
        server.start(wait = true)
    }
}