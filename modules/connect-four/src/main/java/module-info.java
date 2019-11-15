module kb.plugin.connectfour {
    requires java.desktop;

    requires kb.service.api;

    provides kb.service.api.Service
            with kb.plugin.connectfour.CNGService;

    exports kb.plugin.connectfour;
}