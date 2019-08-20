@file:Suppress("unused")

package knotbook.core.fx

import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.TextFlow


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

@FXKtDSL
inline fun scrollPane(builder: ScrollPane.() -> Unit): ScrollPane = ScrollPane().apply(builder)

@FXKtDSL
inline fun gridPane(builder: GridPane.() -> Unit): GridPane = GridPane().apply(builder)

@FXKtDSL
inline fun tabPane(builder: TabPane.() -> Unit): TabPane = TabPane().apply(builder)

@FXKtDSL
inline fun anchorPane(builder: AnchorPane.() -> Unit): AnchorPane = AnchorPane().apply(builder)

@FXKtDSL
inline fun textFlow(builder: TextFlow.() -> Unit): TextFlow = TextFlow().apply(builder)

@FXKtDSL
inline fun stackPane(builder: StackPane.() -> Unit): StackPane = StackPane().apply(builder)