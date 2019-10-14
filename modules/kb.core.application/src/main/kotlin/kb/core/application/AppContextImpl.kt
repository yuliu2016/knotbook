package kb.core.application

import kb.service.api.Service
import kb.service.api.TextEditor
import kb.service.api.TextEditorService
import kb.service.api.application.ApplicationProps
import kb.service.api.application.PrivilegedContext

class AppContextImpl(
        private val ext: List<Service>,
        private val textEdit: List<TextEditorService>,
        private val props: ApplicationProps
) : PrivilegedContext {

    override fun getProps(): ApplicationProps {
        return props
    }

    override fun createTextEditor(): TextEditor {
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