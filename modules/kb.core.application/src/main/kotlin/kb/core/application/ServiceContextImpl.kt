package kb.core.application

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceProps
import kb.service.api.application.PrivilegedContext
import kb.service.api.textedit.TextEditor

class ServiceContextImpl(
        private val service: Service,
        private val context: PrivilegedContext
) : ServiceContext {
    override fun getService(): Service {
        return service
    }

    override fun getProps(): ServiceProps {
        return context.props.getProps(service.metadata.packageName)
    }

    override fun createTextEditor(): TextEditor {
        return context.createTextEditor()
    }
}