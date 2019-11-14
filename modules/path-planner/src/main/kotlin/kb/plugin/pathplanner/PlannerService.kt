package kb.plugin.pathplanner

import javafx.scene.input.KeyCode
import kb.core.fx.combo
import kb.service.api.Service
import kb.service.api.ServiceContext
import kb.service.api.ServiceMetadata
import org.kordamp.ikonli.materialdesign.MaterialDesign

class PlannerService : Service {

    private val metadata = ServiceMetadata()

    init {
        metadata.packageName = "Drive Path Planner"
        metadata.packageVersion = "1.0"
    }

    override fun getMetadata(): ServiceMetadata {
        return metadata
    }

    override fun launch(context: ServiceContext) {

        context.uiManager.registerCommand("path.planner.launch",
                "Launch Path Planner", MaterialDesign.MDI_NAVIGATION.description,
                combo(KeyCode.P, control = true, shift = true)
        ) { PathPlanner() }

        val config = context.config

        val field = config.getJSONObject("Field")

        field["Field Image"] = "None"
        field["Horizontal Origin"] = "Centre"
        field["Vertical Origin"] = "Bottom"
        field["Pixels Per Meter"] = 10.0

        val path = config.getJSONObject("Path")

        path["Heading Conform Factor"] = 1.2
        path["Enable Quintic Splines"] = true
        path["Enable Cubic Splines"] = true
        path["Enable Arcs"] = true
        path["Enable Turning In Place"] = true
        path["Minimum Segment dX"] = 0.5
        path["Minimum Segment dY"] = 0.1
        path["Minimum Segment dTheta"] = 0.1
        path["Iterative Δk² Optimization"] = true
        path["Optimization Passes"] = 100


        val trajectory = config.getJSONObject("Trajectory")

        trajectory["Effective Wheelbase"] = 14.0
        trajectory["Wheelbase Multiplier"] = 1.0
        trajectory["Wheel Radius"] = 3 * 2.54 * 0.01
        trajectory["Max Velocity"] = 9.0
        trajectory["Max Acceleration"] = 7.0
        trajectory["Max Centripetal Acceleration"] = 0.0
        trajectory["Max Jerk"] = 0
        trajectory["Ramped Acceleration Pass"] = false

        val simulation = config.getJSONObject("Simulation")

        simulation["Show Curvature Circle"] = false
        simulation["Small Step"] = 0.2
        simulation["Large Step"] = 0.7

        val plot = config.getJSONObject("Plot")

        plot["Velocity"] = false
        plot["Acceleration"] = false
        plot["Jerk"] = false
        plot["Angular Velocity"] = false
        plot["Angular Acceleration"] = false
        plot["Angular Jerk"] = false
        plot["Time Steps"] = false
    }
}