package kb.core.view.server

import com.sun.net.httpserver.HttpServer
import kb.service.api.array.TableArray
import kb.service.api.array.TableUtil
import java.io.FileInputStream
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class Server {
    private val server: HttpServer = HttpServer.create().apply {

        createContext("/hello") { ex ->
            ex.responseHeaders.add("Content-type", "text/plain")
            ex.sendResponseHeaders(200, 0)
            PrintWriter(ex.responseBody).use { out ->
                out.println("Hello ${ex.remoteAddress.hostName}!")
            }
        }

        createContext("/table") { ex ->
            ex.responseHeaders.add("Content-type", "text/html")
            ex.sendResponseHeaders(200, 0)
            val r0 = TableUtil.toHTML(TableArray
                    .fromCSV(FileInputStream("C:/Users/Yu/Desktop/windsor.csv"), true))

            val response = "<!DOCTYPE HTML><html><head><title>windsor.csv</title></head><body>$r0</body></html>".toByteArray()
            ex.responseBody.use { it.write(response) }
        }

        executor = Executors.newSingleThreadExecutor {
            val thread = Executors.defaultThreadFactory().newThread(it)
            thread.isDaemon = true
            thread
        }
    }

    fun bindAndStart() {
        server.bind(InetSocketAddress(8650), 0)
        server.start()
    }

    fun exit() {
        server.stop(0)
    }
}