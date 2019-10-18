package kb.core.application

import kb.service.api.Service
import kb.service.api.application.ApplicationProps
import kb.service.api.application.ServiceManager
import kb.service.api.ui.TextEditor
import kb.service.api.ui.TextEditorService

class ServiceManagerImpl(
        private val ext: List<Service>,
        private val textEdit: List<TextEditorService>,
        private val props: ApplicationProps
) : ServiceManager {

    override fun getProps(): ApplicationProps {
        return props
    }

    fun createTextEditor(): TextEditor {
        if (textEdit.isNotEmpty()) {
            return textEdit.first().create()
        }
        throw NotImplementedError("no text editor is not implemented")
    }

    override fun getServices(): List<Service> {
        return ext
    }

    override fun getVersion(): String {
        return "3.1.0 - Alpha"
    }
}