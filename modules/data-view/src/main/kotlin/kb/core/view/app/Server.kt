package kb.core.view.app

import com.sun.net.httpserver.HttpServer
import kb.service.api.array.Tables
import kb.service.api.ui.UIHelper
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.util.zip.GZIPOutputStream

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
            ex.responseHeaders.add("Content-Encoding", "gzip")
            ex.sendResponseHeaders(200, 0)
            val r0 = Tables.toHTML(Singleton.uiManager.view?.activeTable?.array)
            val title = Singleton.uiManager.view?.stage?.title
            val response = "<!DOCTYPE HTML><html><head><title>$title</title></head><body>$r0</body></html>"
                    .toByteArray()
            GZIPOutputStream(ex.responseBody).use { it.write(response) }
        }

        executor = UIHelper.createExecutor("Server Executor") { e ->
            Singleton.uiManager.showException(e)
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