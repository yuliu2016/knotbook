package kb.core.application

import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceProps
import kb.service.api.application.ApplicationService
import kb.service.api.ui.CommandManager
import kb.service.api.ui.Notification
import kb.service.api.ui.TextEditor

class ServiceContextImpl(
        private val service: Service,
        private val manager: ServiceManagerImpl,
        private val application: ApplicationService
) : ServiceContext {
    override fun getService(): Service {
        return service
    }

    override fun getProps(): ServiceProps {
        return manager.props.getProps(service.metadata.packageName)
    }

    override fun createTextEditor(): TextEditor {
        return manager.createTextEditor()
    }

    override fun createNotification(): Notification {
        return application.createNotification()
    }

    override fun getCommandManager(): CommandManager {
        return application.commandManager
    }
}