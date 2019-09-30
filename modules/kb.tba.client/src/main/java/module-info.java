module kb.tba.client {
    requires kotlin.stdlib;
    requires fuel;
    requires klaxon;
    requires fuel.coroutines;

    requires kb.service.api;

    provides kb.service.api.Service
            with kb.tba.client.TBAClientService;

    exports kb.tba.client;
}