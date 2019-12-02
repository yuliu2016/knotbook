package kb.plugin.thebluealliance.provider

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import kb.plugin.thebluealliance.api.*
import kb.service.api.ServiceContext
import kb.service.api.array.Tables
import kb.service.api.ui.OptionItem
import kb.service.api.ui.SearchBar
import kb.service.api.ui.UIHelper
import java.util.concurrent.Executor

@Suppress("MemberVisibilityCanBePrivate")
object TBASingleton {
    lateinit var context: ServiceContext
    var tba: TBA? = null

    val executor: Executor = UIHelper.createExecutor("TBA API Executor") { e ->
        context.uiManager.showException(e)
    }

    fun withGetKey(func: (key: String) -> Unit) {
        context.uiManager.getTextInput("Enter the API Key for The Blue Alliance") { key ->
            if (key != null && key.isNotEmpty()) {
                context.config["API Key"] = key
                func(key)
            }
        }
    }

    fun withTBA(forceKey: Boolean = false, func: (TBA) -> Unit) {
        val tba = tba
        if (tba == null || forceKey) {
            withGetKey { key ->
                val newTBA = TBA(key)
                this.tba = newTBA
                executor.execute { func(newTBA) }
            }
        } else {
            executor.execute { func(tba) }
        }
    }

    var data: List<Event> = ArrayList()
    var items: List<OptionItem> = ArrayList()

    fun showEvents() {
        if (data.isNotEmpty()) showEventsBar()
        else withTBA { showEvents0(it) }
    }

    val eventBar = SearchBar()

    fun showEvents0(tba: TBA) {
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
    }

    fun showEventsBar() {
        eventBar.setHint("Search ${items.size} Events")
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
        withTBA { tba ->
            val m = tba.getEventMatchesSimple("${event.year}${event.event_code}")
                    .filter { it.comp_level == "qm" }.sortedBy { it.match_number }
            val a = Tables.ofSize(m.size + 1, 6, true)
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
        }
    }

    fun setKey() {
        withTBA(forceKey = true) {
            val season = it.getStatus().current_season
            context.uiManager.showAlert("Success", "TBA Key is Set. The Current Season is $season")
        }
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
            setKey()
        }
        val config = context.config
        if (config.containsKey("API Key")) {
            tba = TBA(config.getString("API Key"))
        }
        config["Cache Directory"] = "Not Set"
        config["Cache Enabled"] = true
        config["Cache First"] = false
        config["Primary District"] = "Ontario"
        config["Year"] = 2019
    }
}