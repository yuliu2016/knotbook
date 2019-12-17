package kb.core.view.app

import javafx.scene.paint.Color
import kb.core.view.DataTable
import kb.service.api.array.TableArray
import kb.service.api.data.DataSpace
import kb.service.api.ui.UIHelper
import org.kordamp.ikonli.materialdesign.MaterialDesign

class TableSpace : DataSpace {
    override fun newData(title: String, data: TableArray) {
        UIHelper.run {
            val view = Singleton.uiManager.view?: Singleton.newWindow().apply { show() }
            view.addTable(DataTable(title, data, MaterialDesign.MDI_TABLE, Color.RED))
        }
    }
}