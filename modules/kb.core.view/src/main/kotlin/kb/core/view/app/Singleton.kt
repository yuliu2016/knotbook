package kb.core.view.app

import javafx.beans.property.SimpleStringProperty
import kb.service.api.application.PrivilegedContext

internal object Singleton {
    val memoryUsed = SimpleStringProperty()

    var zzNullableContext: PrivilegedContext? = null

    val context by lazy { zzNullableContext!! }
}