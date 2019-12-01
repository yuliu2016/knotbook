package kb.core.view.app

import kb.service.api.array.TableArray
import kb.service.api.data.DataSpace
import kb.service.api.ui.UIHelper

class TableSpace : DataSpace {
    override fun newData(title: String, data: TableArray) {
        UIHelper.run {
            val view = Singleton.uiManager.view
            if (view == null || view.array != null) {
                val dv = Singleton.newWindow()
                dv.setData(title, data)
                dv.show()
            } else {
                view.setData(title, data)
            }
        }
    }
}