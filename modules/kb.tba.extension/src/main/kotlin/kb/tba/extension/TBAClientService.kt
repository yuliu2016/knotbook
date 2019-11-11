package kb.tba.extension

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import kb.service.api.ui.Command
import kb.service.api.ui.UIManager

class TBAClientService : Service {

    private val metadata = ServiceMetadata()

    init {
        metadata.packageName = "kb.tba.client"
        metadata.packageVersion = "3.0"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {
        context.uiManager.apply {
            register("set_key", "Set APIv3 Key")
            register("get_match_schedule", "Get Event Match Schedule")
            register("get_event_rankings", "Get Event Rankings")
            register("get_team_data", "Get Team Data")
            register("update_data", "Update Data")
            register("set_year", "Set Year")
            register("get_opr", "Get Event OPRs")
            register("get_teams", "Get Team List")
            register("get_district_rankings", "Get District Rankings")
            register("set_cache", "Data Caching")
            register("event_list", "Get Event List")
        }
    }

    private fun UIManager.register(id: String, name: String) {
        registerCommand("kb.tba.$id", Command("The Blue Alliance: $name", null, null, null))
    }
}