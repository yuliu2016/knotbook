package kb.application

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.TextEditor
import kb.service.api.TextEditorProvider
import kb.service.api.application.ApplicationService
import kb.service.api.application.PrivilagedContext

class AppContextImpl(
        val ext: List<Service>,
        private val textEdit: TextEditorProvider
) : PrivilagedContext {
    override fun getService(): ApplicationService {
        TODO("not implemented")
    }

    override fun createTextEditor(): TextEditor {
        return textEdit.create()
    }

    override fun getContexts(): Array<ServiceContext> {
        TODO("not implemented")
    }

}