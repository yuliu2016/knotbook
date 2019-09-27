import wpilib
import wpilib.drive
import ctre
import dataclasses

from typing import Optional

from mathlib import *
from robot_io import *
from actionlib import *
from drive import *
from limelight import *
from elevator import *


class Astro(wpilib.TimedRobot):
    io: IO = None

    drive: Drive = None
    limelight: Limelight = None
    elevator: Elevator = None

    elevator_trigger_latch = Latch()

    executor = Executor()

    def robotInit(self):
        if self.isReal():
            self.io = RealRobotIO()
        else:
            self.io = SimulatedIO()

        self.drive = Drive(self.io)
        self.limelight = Limelight(self.io)
        self.elevator = Elevator(self.io)

    def disabledInit(self):
        self.io.disable()

    def autonomousInit(self):
        self.io.enable()

    def teleopInit(self):
        self.io.enable()

    def autonomousPeriodic(self):
        self.io.read_auton_inputs()
        self.sandstorm_loop()
        self.executor.update()
        self.io.write_outputs()

    def teleopPeriodic(self):
        self.io.read_tele_inputs()
        self.teleop_driver_loop()
        self.teleop_operator_loop()
        self.executor.update()
        self.io.write_outputs()

    def sandstorm_loop(self):
        pass

    def update_passthrough(self, speed):
        self.io.outtake_speed = speed * 0.8
        self.io.conveyor_speed = speed * 0.9
        self.io.intake_speed = speed

    def teleop_driver_loop(self):
        d = self.io.driver

        x_speed = deadband(-d.left_y, max_v=1.0, db=0.2)
        z_rotation = deadband(d.right_x, max_v=1.0, db=0.2)
        quick_turn = d.left_bumper == ButtonState.HELD_DOWN
        self.drive.cheesy_drive(x_speed, z_rotation, quick_turn)

        if d.right_bumper == ButtonState.PRESSED:
            self.io.limelight_mode = LimelightMode.VISION
        else:
            self.io.limelight_mode = LimelightMode.DRIVER

        want_aligning = d.right_bumper == ButtonState.HELD_DOWN and not quick_turn
        self.limelight.update_drive_alignment(want_aligning, x_speed)

        if d.left_trigger > 0.2:
            self.update_passthrough(-1 * d.left_trigger)
        elif d.right_trigger > 0.2:
            self.update_passthrough(d.right_trigger)

        if d.b_button == ButtonState.PRESSED:
            self.io.outtake_speed = 1.0  # fast outtake

        if d.a_button == ButtonState.PRESSED:
            if not self.io.pushing:
                self.io.grabbing = False
                self.executor.set_action(
                    WaitFor(t=0.3, func=self.io.invert_pushing),
                    replace_if_busy=False
                )
            else:
                self.io.invert_pushing()
        elif d.x_button == ButtonState.PRESSED:
            self.io.invert_grabbing()
            self.io.pushing = False

    def teleop_operator_loop(self):
        o = self.io.operator
        e = self.elevator

        if abs(o.left_y > 0.2):
            e.manual_speed = deadband(o.left_y, max_v=1.0, db=0.2)
            e.is_manual = True
        else:
            e.manual_speed = 0.0

        if o.right_bumper == ButtonState.PRESSED:
            e.increase_setpoint()
        elif o.left_bumper == ButtonState.PRESSED:
            e.decrease_setpoint()

        elif o.y_button == ButtonState.PRESSED:
            e.setpoint_type = GP.HATCH
            e.setpoint = e.cool_setpoint()
            e.is_manual = False

        elif o.b_button == ButtonState.PRESSED:
            e.setpoint_type = GP.CARGO
            e.setpoint = e.cool_setpoint()
            e.is_manual = False

        elif o.x_button == ButtonState.PRESSED:
            e.setpoint = const.HATCH_1_HEIGHT
            e.is_manual = False

        elif o.a_button == ButtonState.PRESSED:
            e.setpoint = const.ELEVATOR_HOME_HEIGHT
            e.is_manual = False

        elif o.start == ButtonState.PRESSED:
            e.feedforward_on = not e.feedforward_on

        if self.elevator_trigger_latch.update(o.left_trigger > 0.2):
            e.setpoint = const.CARGO_2_HEIGHT - 12.0
            e.is_manual = False

        e.update()


if __name__ == '__main__':
    wpilib.run(Astro)
