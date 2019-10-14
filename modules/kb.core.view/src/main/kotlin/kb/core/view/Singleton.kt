package kb.core.view

import javafx.beans.property.SimpleStringProperty
import kb.service.api.application.PrivilegedContext

object Singleton {
    val memoryUsed = SimpleStringProperty()

    var zzNullableContext: PrivilegedContext? = null

    val context by lazy { zzNullableContext!! }
}