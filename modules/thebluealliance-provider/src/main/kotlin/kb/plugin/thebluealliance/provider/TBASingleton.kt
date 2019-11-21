package kb.plugin.thebluealliance.provider

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import kb.plugin.thebluealliance.api.EventSimple
import kb.plugin.thebluealliance.api.TBA
import kb.plugin.thebluealliance.api.getEventsByYearSimple
import kb.service.api.ServiceContext
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem
import kb.service.api.ui.UIManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("MemberVisibilityCanBePrivate")
object TBASingleton {
    lateinit var context: ServiceContext
    lateinit var tba: TBA
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    var data: List<EventSimple>? = null

    fun showEvents() {
        executor.submit {
            try {
                if (data == null)
                data = tba.getEventsByYearSimple(2019).sortedBy { it.name }

                Platform.runLater { showEventsBar(data!!) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var bar: OptionBar? = null

    fun showEventsBar(events: List<EventSimple>) {
        if (bar == null) {
            val bar = OptionBar()
            for (event in events) {
                bar.items.add(OptionItem(event.name, "${event.year}${event.event_code}", null, null))
            }
            this.bar = bar
        }
        context.uiManager.showOptionBar(bar)
    }

    fun launch(context: ServiceContext) {
        this.context = context

        val m = context.uiManager

        m.registerCommand("tba.get_match_schedule", "Get Event Match Schedule", null, KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN)) {
            showEvents()
        }
        m.apply {
            register("set_key", "Set APIv3 Key")
            register("set_year", "Set Year")
            register("set_district", "Set Primary District")
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
        tba = TBA(config.getString("API Key")) // FIXME
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