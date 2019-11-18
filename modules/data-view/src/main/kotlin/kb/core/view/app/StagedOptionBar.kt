package kb.core.view.app

import javafx.beans.InvalidationListener
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Popup
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import kb.core.view.util.PrettyListView
import kb.service.api.ui.OptionBar
import kb.service.api.ui.OptionItem
import org.kordamp.ikonli.materialdesign.MaterialDesign

@Suppress("MemberVisibilityCanBePrivate")
class StagedOptionBar {

    var optionBar: OptionBar? = null

    val lv = PrettyListView<OptionItem>().apply {
        vgrow()
        isFocusTraversable = false
        maxHeight = 320.0
        setCellFactory { OptionItemCell() }
    }

    val tf = textField {
        this.setOnKeyPressed { e ->
            when {
                e.code == KeyCode.UP || (e.code == KeyCode.TAB && e.isShiftDown) -> {
                    val i = lv.selectionModel.selectedIndex
                    if (i != 0) {
                        lv.selectionModel.select(i - 1)
                        lv.scrollTo(i - 8)
                    } else {
                        lv.selectionModel.select(lv.items.size - 1)
                        lv.scrollTo(lv.items.size - 1)
                    }
                    e.consume()
                }
                e.code == KeyCode.DOWN || e.code == KeyCode.TAB -> {
                    val i = lv.selectionModel.selectedIndex
                    if (i != lv.items.size - 1) {
                        lv.selectionModel.select(i + 1)
                        lv.scrollTo(i - 6)
                    } else {
                        lv.selectionModel.select(0)
                        lv.scrollTo(0)
                    }
                    e.consume()
                }
                e.code == KeyCode.ESCAPE -> {
                    cancel()
                    e.consume()
                }
                e.code == KeyCode.ENTER -> {
                    optionBar?.onEnterPressed?.handle(ActionEvent())
                    e.consume()
                }
            }
        }
        styleClass("formula-field")
    }

    val okButton = button {
        styleClass("ok-button")
        graphic = fontIcon(MaterialDesign.MDI_CHECK, 14)
        isFocusTraversable = false
        setOnAction {
            popup.hide()
            optionBar?.onHideAndContinue?.handle(ActionEvent())
        }
    }

    val topBox = hbox {
        align(Pos.CENTER)
        padding = Insets(6.0)
        spacing = 6.0
        add(tf.hgrow())
    }

    val container = borderPane {
        effect = DropShadow().apply {
            color = Color.rgb(64, 64, 64)
            blurType = BlurType.GAUSSIAN
            height = 10.0
            width = 10.0
            radius = 10.0
            offsetY = 5.0
        }
        styleClass("option-bar")
        prefWidth = 540.0
        top = topBox
        bottom = lv
    }

    val popup = Popup().apply {
        content.setAll(container)
        isAutoHide = true
        showingProperty().addListener(InvalidationListener {
            if (!isShowing) {
                optionBar?.let { unbindAll(it) }
            }
        })
    }

    fun setTheme(vararg styles: String) {
        container.stylesheets.setAll(styles.toList())
    }

    private fun unbindAll(ob: OptionBar) {
        optionBar = null
        tf.promptTextProperty().unbind()
        tf.textProperty().unbindBidirectional(ob.textProperty())
        ob.selectedItemProperty().unbind()
        container.centerProperty().unbind()
        topBox.children.remove(okButton)
    }

    private fun bindAll(ob: OptionBar) {
        optionBar = ob
        lv.items = ob.items
        if (lv.items.isNotEmpty()) {
            lv.selectionModel.select(0)
        }
        if (ob.onHideAndContinue != null) {
            topBox.add(okButton)
        }
        tf.promptTextProperty().bind(ob.hintProperty())
        tf.textProperty().bindBidirectional(ob.textProperty())
        ob.selectedItemProperty().bind(lv.selectionModel.selectedIndexProperty())
        container.centerProperty().bind(ob.arbitraryViewProperty())
        ob.showingProperty().addListener(InvalidationListener {
            if (!ob.isShowing) {
                cancel()
            }
        })
    }

    fun show(ob: OptionBar, stage: Stage) {
        if (popup.isShowing) {
            // unbind the previous option bar
            optionBar?.isShowing = false
        }
        bindAll(ob)
        ob.isShowing = true
        popup.x = stage.x + stage.width / 2.0 - container.prefWidth / 2.0 - 10.0
        popup.y = stage.y + stage.scene.y
        popup.show(stage)
    }

    fun cancel() {
        popup.hide()
    }
}