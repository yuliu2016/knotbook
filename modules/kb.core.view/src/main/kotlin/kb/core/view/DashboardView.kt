package kb.core.view

import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

@Suppress("MemberVisibilityCanBePrivate")
internal class DashboardView {

    internal val indexTree = TreeView<Entity>().apply {
        prefWidth = 300.0
        minWidth = 300.0
        VBox.setVgrow(this, Priority.ALWAYS)
    }
}