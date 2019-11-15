module kb.core.bowline {
    requires kotlin.stdlib;
    requires javafx.controls;

    requires kb.service.api;

    provides kb.service.api.Service with kb.core.bowline.BowlineService;

    exports kb.core.bowline;
}