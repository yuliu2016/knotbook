module kb.core.server {
    requires kotlin.stdlib;
    requires jdk.httpserver;

    requires kb.service.api;

    provides kb.service.api.Service
            with kb.core.server.ServerService;

    exports kb.core.server;
}