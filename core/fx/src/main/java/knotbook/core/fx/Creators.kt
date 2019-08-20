@file:Suppress("unused")

package knotbook.core.fx

import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox


@FXKtDSL
inline fun hbox(builder: HBox.() -> Unit): HBox = HBox().apply(builder)

@FXKtDSL
inline fun vbox(builder: VBox.() -> Unit): VBox = VBox().apply(builder)

@FXKtDSL
inline fun textField(builder: TextField.() -> Unit): TextField = TextField().apply(builder)

@FXKtDSL
inline fun splitPane(builder: SplitPane.() -> Unit): SplitPane = SplitPane().apply(builder)

@FXKtDSL
inline fun canvas(builder: Canvas.() -> Unit): Canvas = Canvas().apply(builder)

@FXKtDSL
inline fun label(builder: Label.() -> Unit): Label = Label().apply(builder)

@FXKtDSL
inline fun checkbox(builder: CheckBox.() -> Unit): CheckBox = CheckBox().apply(builder)

@FXKtDSL
inline fun <T> choiceBox(builder: ChoiceBox<T>.() -> Unit): ChoiceBox<T> = ChoiceBox<T>().apply(builder)

@FXKtDSL
inline fun slider(builder: Slider.() -> Unit): Slider = Slider().apply(builder)