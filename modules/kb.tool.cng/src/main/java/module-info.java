module kb.tool.cng {
    requires java.desktop;

    requires kb.service.api;

    provides kb.service.api.Service
            with kb.tool.cng.CNGService;

    exports kb.tool.cng;
}