@file:Suppress("unused")

package kb.core.fx

import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.image.ImageView
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
inline fun <T> listView(builder: ListView<T>.() -> Unit): ListView<T> = ListView<T>().apply(builder)

@FXKtDSL
inline fun <T> treeView(builder: TreeView<T>.() -> Unit): TreeView<T> = TreeView<T>().apply(builder)

@FXKtDSL
inline fun <S> tableView(builder: TableView<S>.() -> Unit): TableView<S> = TableView<S>().apply(builder)

@FXKtDSL
inline fun <S, T> tableColumn(builder: TableColumn<S, T>.() -> Unit): TableColumn<S, T> =
        TableColumn<S, T>().apply(builder)

@FXKtDSL
inline fun slider(builder: Slider.() -> Unit): Slider = Slider().apply(builder)

@FXKtDSL
inline fun scrollPane(builder: ScrollPane.() -> Unit): ScrollPane = ScrollPane().apply(builder)

@FXKtDSL
inline fun gridPane(builder: GridPane.() -> Unit): GridPane = GridPane().apply(builder)

@FXKtDSL
inline fun tabPane(builder: TabPane.() -> Unit): TabPane = TabPane().apply(builder)

@FXKtDSL
inline fun tab(builder: Tab.() -> Unit): Tab = Tab().apply(builder)

@FXKtDSL
inline fun anchorPane(builder: AnchorPane.() -> Unit): AnchorPane = AnchorPane().apply(builder)

@FXKtDSL
inline fun textFlow(builder: TextFlow.() -> Unit): TextFlow = TextFlow().apply(builder)

@FXKtDSL
inline fun stackPane(builder: StackPane.() -> Unit): StackPane = StackPane().apply(builder)

@FXKtDSL
inline fun accordion(builder: Accordion.() -> Unit): Accordion = Accordion().apply(builder)

@FXKtDSL
inline fun titledPane(builder: TitledPane.() -> Unit): TitledPane = TitledPane().apply(builder)

@FXKtDSL
inline fun contextMenu(builder: ContextMenu.() -> Unit): ContextMenu = ContextMenu().apply(builder)

@FXKtDSL
inline fun button(builder: Button.() -> Unit): Button = Button().apply(builder)

@FXKtDSL
inline fun toggleButton(builder: ToggleButton.() -> Unit): ToggleButton = ToggleButton().apply(builder)

@FXKtDSL
inline fun borderPane(builder: BorderPane.() -> Unit): BorderPane = BorderPane().apply(builder)

@FXKtDSL
inline fun imageView(builder: ImageView.() -> Unit): ImageView = ImageView().apply(builder)