@file:Suppress("FunctionName")

package kb.core.view.app

import kb.core.icon.FontIcon
import kb.core.icon.fontIcon
import kb.service.api.ui.Command
import org.kordamp.ikonli.materialdesign.MaterialDesign.*


fun Entity(
        cat: String,
        name: String,
        icon: FontIcon? = null
) = Command("$cat $name", icon?.iconCode, null, null)

fun getList(): List<Command> = listOf(
        Entity("Table:", "Print", fontIcon(MDI_PRINTER, 14)),
        Entity("Folder:", "Export As Zip Archive", fontIcon(MDI_CLOUD_UPLOAD, 14)),
        Entity("Folder:", "Export As Excel Workbook", fontIcon(MDI_FILE_EXCEL, 14)),
        Entity("Application:", "Properties", fontIcon(MDI_TUNE, 14)),
        Entity("Data:", "Calculate Summary for Selection", fontIcon(MDI_CALCULATOR, 14)),
        Entity("Graph:", "Create Histogram", fontIcon(MDI_CHART_HISTOGRAM, 14)),
        Entity("Tool:", "Connect Four Game", fontIcon(MDI_RECORD, 14)),
        Entity("Tool:", "Robot Path Planner", fontIcon(MDI_NAVIGATION, 14)),
        Entity("Tool:", "WebCam QR Scanner", fontIcon(MDI_CAMERA, 14)),
        Entity("Tool:", "Test Python Editor", fontIcon(MDI_LANGUAGE_PYTHON, 14)),
        Entity("JVM:", "Properties", fontIcon(MDI_COFFEE, 14)),
//        Entity("The Blue Alliance:", "Set APIv3 Key"),
//        Entity("The Blue Alliance:", "Get Event Match Schedule"),
//        Entity("The Blue Alliance:", "Get Event Rankings"),
//        Entity("The Blue Alliance:", "Get Team Data"),
//        Entity("The Blue Alliance:", "Update Data"),
//        Entity("The Blue Alliance:", "Set Year"),
//        Entity("The Blue Alliance:", "Get Event OPRs"),
//        Entity("The Blue Alliance:", "Get Team List"),
//        Entity("The Blue Alliance:", "Get District Rankings"),
//        Entity("The Blue Alliance:", "Data Caching Options"),
        Entity("Spreadsheet View:", "Increment Zoom", fontIcon(MDI_MAGNIFY_PLUS, 14)),
        Entity("Spreadsheet View:", "Decrement Zoom", fontIcon(MDI_MAGNIFY_MINUS, 14)),
        Entity("Spreadsheet View:", "Reset Zoom"),
        Entity("UI:", "Expand Tree", fontIcon(MDI_UNFOLD_MORE, 14)),
        Entity("UI:", "Collapse Tree", fontIcon(MDI_UNFOLD_LESS, 14)),
        Entity("UI:", "Enter Full Screen"),
        Entity("UI:", "Open in New Window"),
        Entity("UI:", "Toggle Colour Theme", fontIcon(MDI_COMPARE, 14)),
        Entity("Table:", "Edit as CSV Text", fontIcon(MDI_FILE_DELIMITED, 14)),
        Entity("Data:", "Reveal in Local Cache"),
        Entity("Data:", "Reveal in Data Source"),
        Entity("JVM:", "Mark for Garbage Collection", fontIcon(MDI_DELETE_SWEEP, 14)),
        Entity("Application:", "Revert Properties to Default"),
        Entity("Application:", "Debug with Scenic View", fontIcon(MDI_CLOUD_OUTLINE, 14)),
        Entity("About:", "Splash Screen", fontIcon(MDI_INFORMATION_OUTLINE, 14)),
        Entity("About:", "GitHub Project", fontIcon(MDI_GITHUB_CIRCLE, 14)),
        Entity("About:", "Open Source Licenses"),
        Entity("Experimental:", "BowlineTableView", fontIcon(MDI_BOWL, 14)),
        Entity("Python:", "Execute A Script", fontIcon(MDI_LANGUAGE_PYTHON, 14)),
        Entity("Scouting App:", "Set Event"),
        Entity("Scouting App:", "Start Scanner", fontIcon(MDI_QRCODE, 14)),
        Entity("Colour Scale:", "Add for Selection", fontIcon(MDI_GRADIENT, 14)),
        Entity("Colour Scale:", "Clear for Selection"),
        Entity("Folder:", "Open in Command Prompt", fontIcon(MDI_CONSOLE, 14)),
        Entity("Table:", "Duplicate Data"),
        Entity("Table:", "Find in Cells", fontIcon(MDI_FILE_FIND, 14)),
        Entity("Git:", "Push", fontIcon(MDI_CLOUD_UPLOAD, 14)),
        Entity("Git:", "Commit", fontIcon(MDI_CHECK, 14)),
        Entity("Git:", "History", fontIcon(MDI_HISTORY, 14)),
        Entity("Git:", "Pull", fontIcon(MDI_SOURCE_PULL, 14))
)