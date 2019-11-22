package kb.plugin.thebluealliance.provider

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import kb.plugin.thebluealliance.api.*
import kb.service.api.ServiceContext
import kb.service.api.array.TableArray
import kb.service.api.ui.OptionItem
import kb.service.api.ui.SearchBar
import kb.service.api.ui.UIManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("MemberVisibilityCanBePrivate")
object TBASingleton {
    lateinit var context: ServiceContext
    lateinit var tba: TBA
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    var data: List<Event> = ArrayList()
    var items: List<OptionItem> = ArrayList()

    fun showEvents() {
        if (data.isNotEmpty()) showEventsBar()
        else executor.submit { showEvents0() }
    }

    val eventBar = SearchBar()

    fun showEvents0() {
        try {
            if (data.isEmpty()) {
                data = tba.getEventsByYear(2019).sortedWith(compareBy(
                        { it.event_type },
                        { it.district?.abbreviation },
                        { it.week },
                        { it.name }
                ))
                items = data.map { event ->
                    val info = when (event.event_type!!) {
                        0 -> "Week ${event.week}"
                        1 -> "${event.district?.abbreviation?.toUpperCase()} Week ${event.week?.plus(1)}"
                        else -> event.event_type_string
                    }
                    OptionItem(event.name, null, info, null, null)
                }
            }
            Platform.runLater { showEventsBar() }
        } catch (e: Exception) {
            Platform.runLater { context.uiManager.showException(e) }
            e.printStackTrace()
        }
    }

    fun showEventsBar() {
        eventBar.setItems(items)
        eventBar.setHandler { getData(data[it]) }
        context.uiManager.showOptionBar(eventBar.toOptionBar())
    }

    fun String.toTeam(): Int {
        return substring(3).toInt()
    }

    fun MatchSimple.getTeam(i: Int): Int {
        val red = alliances?.red?.team_keys!!
        val blue = alliances?.blue?.team_keys!!
        return when (i) {
            0 -> red[0].toTeam()
            1 -> red[1].toTeam()
            2 -> red[2].toTeam()
            3 -> blue[0].toTeam()
            4 -> blue[1].toTeam()
            5 -> blue[2].toTeam()
            else -> throw IllegalStateException()
        }
    }

    fun getData(event: Event) {
        executor.submit {
            try {
                val m = tba.getEventMatchesSimple("${event.year}${event.event_code}")
                        .filter { it.comp_level == "qm" }.sortedBy { it.match_number }
                val a = TableArray.ofSize(m.size + 1, 6)
                a[0, 0] = "Red 1"
                a[0, 1] = "Red 2"
                a[0, 2] = "Red 3"
                a[0, 3] = "Blue 1"
                a[0, 4] = "Blue 2"
                a[0, 5] = "Blue 3"
                for (i in m.indices) {
                    for (j in 0..5) {
                        a[i + 1, j] = m[i].getTeam(j)
                    }
                }
                Platform.runLater { context.dataSpace.newData(event.name + " Match Schedule", a) }
            } catch (e: Exception) {
                Platform.runLater { context.uiManager.showException(e) }
                e.printStackTrace()
            }
        }
    }

    fun getData() {

    }

    fun launch(context: ServiceContext) {
        this.context = context

        val m = context.uiManager

        m.registerCommand("tba.get_match_schedule", "The Blue Alliance: Get Event Match Schedule",
                null, KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN)) {
            showEvents()
        }
        m.registerCommand("tba.set_key", "The Blue Alliance: Set APIv3 Key", "mdi-key",
                KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN)) {
            m.getTextInput("Enter the API Key for The Blue Alliance", null) {
                println(it)
            }
        }
        m.registerCommand("tba.get_win", "The Blue Alliance: Get Windsor Matches", null,
                KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)) {
            getData()
        }
        m.apply {
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