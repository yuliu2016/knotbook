package kb.core.view.app

import javafx.scene.paint.Color
import kb.core.view.Tab
import kb.service.api.array.TableArray
import kb.service.api.data.DataSpace
import kb.service.api.ui.UIHelper
import org.kordamp.ikonli.materialdesign.MaterialDesign

class TableSpace : DataSpace {
    override fun newData(title: String, data: TableArray) {
        UIHelper.run {
            val view = Singleton.uiManager.view?: Singleton.newWindow().apply { show() }
            view.setData(title, data)
            view.addTab(Tab(title, MaterialDesign.MDI_TABLE, Color.RED))
            view.selectTab(view.tabs.size -1)
//            if (view == null || view.array != null) {
//                val dv = Singleton.newWindow()
//                dv.setData(title, data)
//                dv.show()
//            } else {
//                view.setData(title, data)
//            }
        }
    }
}