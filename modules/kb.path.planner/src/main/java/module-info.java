module kb.pathplanner {

    requires kotlin.stdlib;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;

    requires ca.warp_seven.frc;

    requires kb.core.icon;
    requires org.kordamp.ikonli.materialdesign;

    requires kb.service.api;
    requires kb.core.fx;

    provides kb.service.api.Service
            with kb.path.planner.PlannerService;

    exports kb.path.planner;
}