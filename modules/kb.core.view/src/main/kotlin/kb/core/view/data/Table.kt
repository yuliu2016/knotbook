package kb.core.view.data

import javafx.collections.ObservableList
import kb.service.api.array.TableArray
import org.controlsfx.control.spreadsheet.SpreadsheetCell
import java.lang.ref.WeakReference

class Table {
    var array: TableArray? = null
    val referenceOrder: WeakReference<ObservableList<ObservableList<SpreadsheetCell>>> =
            WeakReference(null)

}