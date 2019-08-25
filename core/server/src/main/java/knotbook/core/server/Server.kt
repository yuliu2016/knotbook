package knotbook.core.server

import com.sun.net.httpserver.HttpServer
import java.io.PrintWriter
import java.net.InetSocketAddress

class Server {
    init {
        HttpServer.create(InetSocketAddress(8080), 0).apply {

            createContext("/hello") { http ->
                http.responseHeaders.add("Content-type", "text/plain")
                http.sendResponseHeaders(200, 0)
                PrintWriter(http.responseBody).use { out ->
                    out.println("Hello ${http.remoteAddress.hostName}!")
                }
            }

            start()
        }
    }
}