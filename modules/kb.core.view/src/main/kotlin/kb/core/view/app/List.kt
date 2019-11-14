@file:Suppress("FunctionName")

package kb.core.view.app

import kb.service.api.ui.Command
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.materialdesign.MaterialDesign.*


fun Entity(
        name: String,
        icon: Ikon? = null
) = Command(name, icon?.description, null, null)

fun getList(): List<Command> = listOf(
        Entity("Table: Print", MDI_PRINTER),
        Entity("Folder: Export As Excel Workbook", MDI_FILE_EXCEL),
        Entity("Data: Calculate Summary for Selection", MDI_CALCULATOR),
        Entity("Graph: Create Histogram", MDI_CHART_HISTOGRAM),
        Entity("Spreadsheet View: Increment Zoom", MDI_MAGNIFY_PLUS),
        Entity("Spreadsheet View: Decrement Zoom", MDI_MAGNIFY_MINUS),
        Entity("Spreadsheet View: Reset Zoom"),
        Entity("UI: Open in New Window"),
        Entity("UI: Toggle Colour Theme", MDI_COMPARE),
        Entity("Table: Edit as CSV Text", MDI_FILE_DELIMITED),
        Entity("Data: Reveal in Local Cache"),
        Entity("Data: Reveal in Data Source"),
        Entity("JVM: Mark for Garbage Collection", MDI_DELETE_SWEEP),
        Entity("Application: Revert Properties to Default"),
        Entity("Application: Debug with Scenic View", MDI_CLOUD_OUTLINE),
        Entity("About: Splash Screen", MDI_INFORMATION_OUTLINE),
        Entity("About: GitHub Project", MDI_GITHUB_CIRCLE),
        Entity("About: Open Source Licenses"),
        Entity("Experimental: BowlineTableView", MDI_BOWL),
        Entity("Python: Execute A Script", MDI_LANGUAGE_PYTHON),
        Entity("Scouting App: Set Event"),
        Entity("Scouting App: Start Scanner", MDI_QRCODE),
        Entity("Colour Scale: Add for Selection", MDI_GRADIENT),
        Entity("Colour Scale: Clear for Selection"),
        Entity("Folder: Open in Command Prompt", MDI_CONSOLE),
        Entity("Table: Duplicate Data"),
        Entity("Table: Find in Cells", MDI_FILE_FIND)
)