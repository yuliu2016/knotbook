package kb.tool.path.planner

import javafx.beans.InvalidationListener
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TabPane
import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import kb.core.fx.*
import kb.core.icon.fontIcon
import org.kordamp.ikonli.materialdesign.MaterialDesign.*

@Suppress("MemberVisibilityCanBePrivate")
class PathPlanner {

    val stage = Stage()

    val pathCanvas = PathCanvas()

    @Suppress("UNUSED_CHANGED_VALUE", "UNUSED_VARIABLE")
    val editor = tabPane {
        prefWidth = 320.0
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabDragPolicy = TabPane.TabDragPolicy.FIXED
        modify {
            +tab {
                tooltip = Tooltip("Field Model Configuration")
                graphic = fontIcon(MDI_FLAG, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0

                    addRow(r++, label { text = "Horizontal Origin" }, choiceBox<String> {
                        items.setAll(
                                "Left",
                                "Centre",
                                "Right"
                        )
                        selectionModel.select(1)
                    })
                    addRow(r++, label { text = "Vertical Origin" }, choiceBox<String> {
                        items.setAll(
                                "Top",
                                "Centre",
                                "Bottom"
                        )
                        selectionModel.select(2)
                    })
                    addRow(r++, label { text = "Presets" }, choiceBox<String> {
                        items.setAll(
                                "FRC 2019",
                                "FRC 2018"
                        )
                    })
                    addRow(r++, label { text = "Background Image" }, Button("Choose"))
                    addRow(r++, label { text = "Pixels per Meter" }, textField { })
                    addRow(r++, label { text = "Point Model" }, textField { })
                }
            }
            +tab {
                tooltip = Tooltip("Path Configuration")
                graphic = fontIcon(MDI_VECTOR_CURVE, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0

                    addRow(r++, label { text = "Heading Conform Factor" }, textField { })
                    addRow(r++, label { text = "Enable Quintic Splines" }, checkbox { })
                    addRow(r++, label { text = "Enable Cubic Splines" }, checkbox { })
                    addRow(r++, label { text = "Enable Arcs" }, checkbox { })
                    addRow(r++, label { text = "Enable Turning In Place" }, checkbox { })
                    addRow(r++, label { text = "Minimum Segment dX" }, textField { })
                    addRow(r++, label { text = "Minimum Segment dY" }, textField { })
                    addRow(r++, label { text = "Minimum Segment dTheta" }, textField { })
                    addRow(r++, label { text = "Iterative Δk² Optimization" }, checkbox { })
                    addRow(r++, label { text = "Optimization Passes" }, textField { })
                    addRow(r++, label { text = "Effective Wheelbase" }, textField { })
                    addRow(r++, label { text = "Wheelbase Multiplier" }, slider { })
                    addRow(r++, label { text = "Wheel Radius" }, textField { })
                }
            }
            +tab {
                tooltip = Tooltip("Trajectory Configuration")
                graphic = fontIcon(MDI_CLOCK, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0
                    addRow(r++, label { text = "Max Velocity" }, textField { })
                    addRow(r++, label { text = "Max Velocity Multiplier" }, slider { })
                    addRow(r++, label { text = "Max Acceleration" }, textField { })
                    addRow(r++, label { text = "Max Acceleration Multiplier" }, slider { })
                    addRow(r++, label { text = "Max Centripetal Acceleration" }, textField { })
                    addRow(r++, label { text = "Max Centripetal Multiplier" }, slider { })
                    addRow(r++, label { text = "Max Jerk" }, textField { })
                    addRow(r++, label { text = "Max Jerk Multiplier" }, slider { })
                    addRow(r++, label { text = "Ramped Acceleration Pass" }, checkbox { })
                }
            }
            +tab {
                tooltip = Tooltip("Waypoints")
                graphic = fontIcon(MDI_DIRECTIONS, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0

                }
            }
            +tab {
                tooltip = Tooltip("Path Rendering")
                graphic = fontIcon(MDI_FORMAT_COLOR_FILL, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0

                    addRow(r++, label { text = "Show Background" }, checkbox { })
                    addRow(r++, label { text = "Show Curvature Gradients" }, checkbox { })
                    addRow(r++, label { text = "Robot Width" }, textField { })
                    addRow(r++, label { text = "Robot Length" }, textField { })
                }
            }
            +tab {
                tooltip = Tooltip("Plot Rendering")
                graphic = fontIcon(MDI_CHART_LINE, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0

                    addRow(r++, label { text = "Velocity" }, checkbox { })
                    addRow(r++, label { text = "Acceleration" }, checkbox { })
                    addRow(r++, label { text = "Jerk" }, checkbox { })
                    addRow(r++, label { text = "Angular Velocity" }, checkbox { })
                    addRow(r++, label { text = "Angular Acceleration" }, checkbox { })
                    addRow(r++, label { text = "Angular Jerk" }, checkbox { })
                    addRow(r++, label { text = "Time Steps" }, checkbox { })
                }
            }
            +tab {
                tooltip = Tooltip("Trajectory Simulation")
                graphic = fontIcon(MDI_PLAY, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0

                    addRow(r++, label { text = "Show Curvature Circle" }, checkbox { })
                    addRow(r++, label { text = "Small Incremental Step" }, textField { })
                    addRow(r++, label { text = "Large Incremental Step" }, textField { })
                    addRow(r++, label { text = "Pause on Space Key" }, checkbox { })
                }
            }
            +tab {
                tooltip = Tooltip("Measure and Compare")
                graphic = fontIcon(MDI_RULER, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0
                }
            }
            +tab {
                tooltip = Tooltip("Load and Save")
                graphic = fontIcon(MDI_CONTENT_SAVE, 14)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0
                }
            }
        }
    }

    @Suppress("UNUSED_CHANGED_VALUE")
    val scene = Scene(hbox {
        prefHeight = 600.0
        add(pathCanvas.canvas)
        add(vbox {
            add(vbox {
                prefHeight = 300.0
            })
            add(editor)
        })
    })

    fun updateMainCanvas() {
        pathCanvas.canvas.height = stage.height
        pathCanvas.canvas.width = stage.height / 3.0 * 2.0
        pathCanvas.draw {
            fill = Color.BLACK
        }
    }

    init {
        stage.title = "Differential Drive Path/Trajectory Planner"
        stage.scene = scene
        stage.heightProperty().addListener(InvalidationListener {
            updateMainCanvas()
        })
        stage.maxWidth = Screen.getPrimary().visualBounds.height / 3.0 * 2.0 + 300.0
        stage.width = stage.maxWidth
        stage.show()
    }
}