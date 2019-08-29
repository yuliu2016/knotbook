package kb.core.view

import javafx.geometry.Pos
import javafx.scene.control.TreeView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import kb.core.fx.*
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid

@Suppress("MemberVisibilityCanBePrivate")
internal class DashboardView {

    private fun sectionIconButton(ic: Ikon): VBox = vbox {
        modify {
            +fontIcon(ic, 13)
        }
        styleClass("section-icon-button")
        align(Pos.CENTER)
        minWidth = 28.0
    }

    internal val openButton = sectionIconButton(FontAwesomeSolid.FOLDER_OPEN)
    internal val expandIndexTreeButton = sectionIconButton(FontAwesomeSolid.EXPAND)
    internal val compressIndexTreeButton = sectionIconButton(FontAwesomeSolid.COMPRESS)
    internal val locateButton = sectionIconButton(FontAwesomeSolid.MOUSE_POINTER)

    internal val indexTree = TreeView<Index>().apply {
        prefWidth = 256.0
        minWidth = 256.0
        VBox.setVgrow(this, Priority.ALWAYS)
    }
}