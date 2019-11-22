package kb.core.view.app

import kb.service.api.array.TableArray
import kb.service.api.data.DataSpace

class TableSpace : DataSpace {
    override fun newData(title: String, data: TableArray) {
        val dv = Singleton.newWindow()
        dv.setData(title, data)
        dv.show()
    }
}