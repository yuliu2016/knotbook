package kb.core.view

import javafx.scene.paint.Color
import kb.core.icon.fontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign.*

fun getList(): List<Entity> = listOf(
        Entity("Git:", "Push", fontIcon(MDI_CLOUD_UPLOAD, 14), Color.BLUE),
        Entity("Git:", "Commit", fontIcon(MDI_CHECK, 14), Color.BLUE),
        Entity("Git:", "History", fontIcon(MDI_HISTORY, 14), Color.BLUE),
        Entity("Git:", "Pull", fontIcon(MDI_SOURCE_PULL, 14), Color.BLUE),
        Entity("Table:", "Print", fontIcon(MDI_PRINTER, 14), Color.RED),
        Entity("Folder:", "Export As Zip Archive", fontIcon(MDI_CLOUD_UPLOAD, 14), Color.PURPLE),
        Entity("Folder:", "Export As Excel Workbook", fontIcon(MDI_FILE_EXCEL, 14), Color.PURPLE),
        Entity("Application Properties", null, fontIcon(MDI_TUNE, 14)),
        Entity("Count:", "10", fontIcon(MDI_CALCULATOR, 14), Color.DARKGREEN),
        Entity("Average:", "1.341233", fontIcon(MDI_CALCULATOR, 14), Color.DARKGREEN),
        Entity("StdDev:", "2.3", fontIcon(MDI_CALCULATOR, 14), Color.DARKGREEN),
        Entity("Mean:", "4", fontIcon(MDI_CALCULATOR, 14), Color.DARKGREEN),
        Entity("Mode:", "2", fontIcon(MDI_CALCULATOR, 14), Color.DARKGREEN),
        Entity("Graph:", "Create Histogram", fontIcon(MDI_CHART_HISTOGRAM, 14), Color.ORANGE),
        Entity("Connect Four Game", null, fontIcon(MDI_RECORD, 14)),
        Entity("Tool:", "Robot Path Planner", fontIcon(MDI_NAVIGATION, 14)),
        Entity("Tool:", "WebCam QR Scanner", fontIcon(MDI_CAMERA, 14)),
        Entity("Tool:", "Test Python Editor", fontIcon(MDI_LANGUAGE_PYTHON, 14)),
        Entity("JVM Properties", null, fontIcon(MDI_COFFEE, 14))
)