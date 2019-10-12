from robot_io import *
from mathlib import *
from wpilib import Timer


class Limelight:
    vision_pid = PID(kP=0.2, kI=0.06, kD=0.0)
    pid = PIDController(vision_pid, max_output=0.4)
    dt = Delta()

    def __init__(self, io: IO):
        self.io = io

    def update_drive_alignment(self, want_aligning, x_speed):

        is_aligning = want_aligning and self.io.found_vision_target \
                      and x_speed >= 0 and abs(self.io.vision_error_x) < 15

        if not is_aligning:
            return

        speed_limit = 0.8 - 0.5 * self.io.visionArea
        self.io.left_demand = limit(self.io.left_demand, speed_limit)
        self.io.right_demand = limit(self.io.right_demand, speed_limit)

        if x_speed == 0.0:
            self.io.left_demand += 0.1
            self.io.right_demand += 0.1

        correction = self.pid.update_by_error(math.radians(-self.io.vision_error_x),
                                              self.dt.update(Timer.getFPGATimestamp()))
        if correction > 0:
            self.io.right_demand += correction
        elif correction < 0:
            self.io.left_demand += correction
