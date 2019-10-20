module kb.tba.extension {
    requires kb.service.api;
    requires kotlin.stdlib;
    requires javafx.controls;
    requires kb.tba.client;

    provides kb.service.api.Service
            with kb.tba.extension.TBAClientService;
}