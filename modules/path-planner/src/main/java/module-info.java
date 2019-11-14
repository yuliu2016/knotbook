import kb.plugin.pathplanner.PlannerService;

module kb.plugin.pathplanner {

    requires kotlin.stdlib;

    requires ca.warp_seven.frc;

    requires kb.core.icon;
    requires org.kordamp.ikonli.materialdesign;

    requires kb.service.api;
    requires kb.core.fx;

    provides kb.service.api.Service
            with PlannerService;

    exports kb.plugin.pathplanner;
}