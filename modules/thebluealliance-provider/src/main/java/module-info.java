module kb.plugin.thebluealliance.provider {
    requires kb.service.api;
    requires kotlin.stdlib;
    requires javafx.controls;
    requires kb.plugin.thebluealliance.api;
    requires org.json;

    provides kb.service.api.Service
            with kb.plugin.thebluealliance.provider.TBAClientService;
}