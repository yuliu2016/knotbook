package knotbook.pathplanner

import javafx.beans.InvalidationListener
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TabPane
import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import knotbook.core.fx.*
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid

@Suppress("MemberVisibilityCanBePrivate")
class PathPlanner {

    val stage = Stage()

    val pathCanvas = PathCanvas()

    @Suppress("UNUSED_CHANGED_VALUE", "UNUSED_VARIABLE")
    val editor = tabPane {
        prefWidth = 340.0
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabDragPolicy = TabPane.TabDragPolicy.FIXED
        modify {
            +tab {
                // isAnimated = false
//                text = "Field Model Configuration"
                graphic = fontIcon(FontAwesomeSolid.FLAG, 13)
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
                // isAnimated = false
//                text = "Path Configuration"
                graphic = fontIcon(FontAwesomeSolid.BEZIER_CURVE, 13)
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
                }
            }
            +tab {
                // isAnimated = false
                tooltip = Tooltip("Robot Configuration")
                graphic = fontIcon(FontAwesomeSolid.RULER, 13)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0
                    addRow(r++, label { text = "Effective Wheelbase" }, textField { })
                    addRow(r++, label { text = "Wheelbase Multiplier" }, slider { })
                    addRow(r++, label { text = "Wheel Radius" }, textField { })
                    addRow(r++, label { text = "kV" }, textField { })
                    addRow(r++, label { text = "kA" }, textField { })
                }
            }
            +tab {
                // isAnimated = false
                tooltip = Tooltip("Trajectory Configuration")
                graphic = fontIcon(FontAwesomeSolid.CLOCK, 13)
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
                // isAnimated = false
//                text = "Waypoint List"
                graphic = fontIcon(FontAwesomeSolid.DIRECTIONS, 13)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0

                }
            }
            +tab {
                // isAnimated = false
//                text = "Path Rendering"
                graphic = fontIcon(FontAwesomeSolid.PENCIL_ALT, 13)
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
                // isAnimated = false
//                text = "Plot Rendering"
                graphic = fontIcon(FontAwesomeSolid.CHART_LINE, 13)
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
                    addRow(r++, label { text = "Time Steps" }, checkbox { })

                }
            }
            +tab {
                // isAnimated = false
//                text = "Trajectory Simulation"
                graphic = fontIcon(FontAwesomeSolid.PLAY, 12)
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
                // isAnimated = false
//                text = "Compare"
                graphic = fontIcon(FontAwesomeSolid.CHECK, 13)
                content = gridPane {
                    padding = Insets(8.0)
                    hgap = 8.0
                    vgap = 4.0
                    var r = 0
                }
            }
            +tab {
                // isAnimated = false
                text = " "
                graphic = fontIcon(FontAwesomeSolid.SAVE, 13)
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
        prefHeight = 800.0
        add(pathCanvas.canvas)
        add(editor)
    })

    fun updateMainCanvas() {
        pathCanvas.canvas.height = stage.height
        pathCanvas.canvas.width = stage.height / 3.0 * 2.0
        pathCanvas.draw {
            fill = Color.BLACK
//            fillRect(0.0, 0.0, pathCanvas.theCanvas.width, pathCanvas.theCanvas.height)
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
//        runLater { updateMainCanvas() }
        stage.show()
    }
}