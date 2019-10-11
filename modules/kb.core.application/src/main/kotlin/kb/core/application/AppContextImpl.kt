package kb.core.application

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.TextEditor
import kb.service.api.TextEditorService
import kb.service.api.application.ApplicationProps
import kb.service.api.application.ApplicationService
import kb.service.api.application.PrivilagedContext

class AppContextImpl(
        val ext: List<Service>,
        private val textEdit: TextEditorService,
        private val props: ApplicationProps
) : PrivilagedContext {

    override fun getProps(): ApplicationProps {
        return props
    }

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