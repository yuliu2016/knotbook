package kb.plugin.thebluealliance.provider

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import kb.service.api.ui.UIManager

class TBAClientService : Service {

    private val metadata = ServiceMetadata()

    init {
        metadata.packageName = "The Blue Alliance Integration"
        metadata.packageVersion = "3.0"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {
        context.uiManager.apply {
            register("set_key", "Set APIv3 Key")
            register("set_year", "Set Year")
            register("set_district", "Set Primary District")
            register("get_match_schedule", "Get Event Match Schedule")
            register("get_event_rankings", "Get Event Rankings")
            register("get_team_data", "Get Team Data")
            register("get_opr", "Get Event OPRs")
            register("get_teams", "Get Team List")
            register("get_district_rankings", "Get District Rankings")
            register("set_cache", "Data Caching")
            register("event_list", "Get Event List")
            register("event_predictions", "Get Event Predictions")
            register("event_list", "Get Event Match Results")
        }
        val config = context.config
        config["API Key"] = ""
        config["Cache Directory"] = "Not Set"
        config["Cache Enabled"] = true
        config["Cache First"] = false
        config["Primary District"] = "Ontario"
        config["Year"] = 2019
    }

    private fun UIManager.register(id: String, name: String) {
        registerCommand("tba.$id", "The Blue Alliance: $name", null, null, null)
    }
}