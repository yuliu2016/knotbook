package knotbook.core.server

import io.ktor.application.Application
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class Server {
    init {
        embeddedServer(Netty) {
            routing {
                get("/hi") {

                }

                post("/status") {

                }
            }
        }.start(wait = true)
    }
}