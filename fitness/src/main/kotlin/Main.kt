import event.EventsRepository
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import server.manager
import server.report
import server.turnstile
import system.Manager
import system.Report
import system.Turnstile

fun main(args: Array<String>) {
    val repository = EventsRepository()
    manager = Manager(repository)
    turnstile = Turnstile(repository)
    report = Report(repository)
    embeddedServer(
        factory = Netty,
        environment = commandLineEnvironment(args)
    ).start(wait = true)
}