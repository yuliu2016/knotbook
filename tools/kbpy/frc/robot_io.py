import wpilib

from wpilib.interfaces.generichid import GenericHID

import networktables

import ctre
from ctre.basemotorcontroller import BaseMotorController
# noinspection PyProtectedMember
from ctre.basemotorcontroller import DemandType

import navx

import enum
from abc import ABC, abstractmethod

from mathlib import *

import const
from const import MCMode, LimelightMode


class ButtonState(enum.IntEnum):
    NONE = 0
    RELEASED = 1
    PRESSED = 2
    HELD_DOWN = 3


class RobotController:
    a_button = ButtonState.NONE
    b_button = ButtonState.NONE
    x_button = ButtonState.NONE
    y_button = ButtonState.NONE
    left_bumper = ButtonState.NONE
    right_bumper = ButtonState.NONE
    left_stick = ButtonState.NONE
    right_stick = ButtonState.NONE
    start = ButtonState.NONE
    back = ButtonState.NONE
    left_x = 0.0
    left_y = 0.0
    right_x = 0.0
    right_y = 0.0
    left_trigger = 0.0
    right_trigger = 0.0


class IO(ABC):
    def __init__(self):
        self.driver = RobotController()
        self.operator = RobotController()

        # Inputs

        self.left_position: float = 0.0  # rad
        self.right_position: float = 0.0  # rad
        self.left_velocity: float = 0.0  # rad/s
        self.right_velocity: float = 0.0  # rad/s

        self.elevator_position: int = 0  # ticks
        self.elevator_velocity: int = 0  # ticks/100ms
        self.hall_effect_triggered: bool = False

        self.limelight_connected: bool = False
        self.found_vision_target: bool = False
        self.vision_error_x: float = 0.0  # deg
        self.vision_error_y: float = 0.0  # deg
        self.vision_area: float = 0.0  # % of full image

        self.gyro_connected: bool = False
        self.fused_heading: float = 0.0  # rad
        self.previous_yaw: rotation = rad(0)
        self.yaw: rotation = rad(0)
        self.angular_velocity: float = 0.0  # rad/s

        # Outputs

        self.drive_control_mode = ctre.ControlMode.PercentOutput
        self.left_demand: float = 0.0
        self.right_demand: float = 0.0
        self.left_feedforward: float = 0.0  # [-1, 1]
        self.right_feedforward: float = 0.0  # [-1, 1]

        self.elevator_control_mode = ctre.ControlMode.PercentOutput
        self.elevator_demand: float = 0.0
        self.elevator_feedforward: float = 0.0

        self.intake_speed: float = 0.0
        self.conveyor_speed: float = 0.0
        self.outtake_speed: float = 0.0

        self.pushing: bool = False
        self.grabbing: bool = False

        # Configurations

        self.drive_pid: PID
        self.drive_ramp_rate: float = 0.0

        self.elevator_pid: PID
        self.elevator_ramp_rate: float = 0.0

        self.limelight_mode: LimelightMode = LimelightMode.VISION

    def invert_grabbing(self):
        self.grabbing = not self.grabbing

    def invert_pushing(self):
        self.pushing = not self.pushing

    @abstractmethod
    def reset_drive_position(self, positionRadians: float):
        pass

    @abstractmethod
    def reset_elevator_position(self, positionRadians: float):
        pass

    @abstractmethod
    def initialize(self):
        pass

    @abstractmethod
    def enable(self):
        pass

    @abstractmethod
    def disable(self):
        pass

    @abstractmethod
    def read_tele_inputs(self):
        pass

    @abstractmethod
    def read_auton_inputs(self):
        pass

    @abstractmethod
    def write_outputs(self):
        pass


class SimulatedIO(IO):
    def reset_drive_position(self, positionRadians: float):
        pass

    def reset_elevator_position(self, positionRadians: float):
        pass

    def initialize(self):
        pass

    def enable(self):
        pass

    def disable(self):
        pass

    def read_tele_inputs(self):
        pass

    def read_auton_inputs(self):
        pass

    def write_outputs(self):
        pass


def _u(old: ButtonState, new: bool):
    if new:
        if old == ButtonState.HELD_DOWN or old == ButtonState.PRESSED:
            return ButtonState.HELD_DOWN
        else:
            return ButtonState.PRESSED
    else:
        if old == ButtonState.NONE or old == ButtonState.RELEASED:
            return ButtonState.NONE
        else:
            return ButtonState.RELEASED


_L = GenericHID.Hand.kLeft
_R = GenericHID.Hand.kRight


def _update_xbox(c: "RobotController", x: "wpilib.XboxController"):
    c.left_x = x.getX(_L)
    c.left_y = x.getY(_L)
    c.right_x = x.getX(_R)
    c.right_y = x.getY(_R)
    c.left_trigger = x.getTriggerAxis(_L)
    c.right_trigger = x.getTriggerAxis(_R)
    c.a_button = _u(c.a_button, x.getAButton())
    c.b_button = _u(c.b_button, x.getBButton())
    c.x_button = _u(c.x_button, x.getXButton())
    c.y_button = _u(c.y_button, x.getYButton())
    c.start = _u(c.start, x.getStartButton())
    c.back = _u(c.back, x.getBackButton())
    c.left_bumper = _u(c.left_bumper, x.getBumper(_L))
    c.right_bumper = _u(c.right_bumper, x.getBumper(_R))
    c.left_stick = _u(c.left_stick, x.getStickButton(_L))
    c.right_stick = _u(c.right_stick, x.getStickButton(_R))


def _talon_srx(can_id: int, followers: Optional[Iterable[BaseMotorController]] = None):
    t = ctre.TalonSRX(can_id)
    t.configFactoryDefault()
    t.enableVoltageCompensation(True)
    if followers is not None:
        for follower in followers:
            follower.follow(t)
    return t


def _victor_spx(can_id: int, neutralMode=ctre.NeutralMode.Brake):
    v = ctre.VictorSPX(can_id)
    v.setNeutralMode(neutralMode.value)
    v.configFactoryDefault()
    return v


class RealRobotIO(IO):

    def __init__(self):
        super().__init__()
        self.left = _talon_srx(const.DRIVE_LEFT, followers=[
            _victor_spx(const.LEFT_FOLLOWER_A),
            _victor_spx(const.LEFT_FOLLOWER_B)
        ])

        self.right = _talon_srx(const.DRIVE_RIGHT, followers=[
            _victor_spx(const.RIGHT_FOLLOWER_A),
            _victor_spx(const.RIGHT_FOLLOWER_B)
        ])

        self.intake = self.victor(const.INTAKE, neutralMode=ctre.NeutralMode.Coast)
        self.conveyor_left = self.victor(const.CONVEYOR_LEFT)
        self.conveyor_right = self.victor(const.CONVEYOR_RIGHT)

        self.outtake_left = self.victor(const.OUTTAKE_LEFT)
        self.outtake_right = self.victor(const.OUTTAKE_RIGHT)

        self.pusher = wpilib.Solenoid(const.PUSHER_SOLENOID)
        self.grabber = wpilib.Solenoid(const.GRABBER_SOLENOID)

        elevator_follower = self.victor(const.ELEVATOR_FOLLOWER)
        elevator_follower.setInverted(true)
        self.elevator = self.talon(const.DRIVE_LEFT, followers=[elevator_follower])
        self.elevator.setSensorPhase(true)

        self.hall_effect = wpilib.DigitalInput(const.ELEVATOR_HALL_EFFECT)
        self.ahrs = navx.AHRS(wpilib.SPI.Port.kMXP, update_rate_hz=100)

        self.driver_xbox = wpilib.XboxController(0)
        self.operator_xbox = wpilib.XboxController(1)

    def write_outputs(self):
        self.left.set(self.drive_control_mode, self.left_demand,
                      DemandType.ArbitraryFeedForward, self.left_feedforward)
        self.right.set(self.drive_control_mode, self.right_demand,
                       DemandType.ArbitraryFeedForward, self.right_feedforward)
        self.elevator.set(self.elevator_control_mode, self.elevator_demand,
                          DemandType.ArbitraryFeedForward, self.elevator_feedforward)

        self.conveyor_left.set(ctre.ControlMode.PercentOutput, self.conveyor_speed)
        self.conveyor_right.set(ctre.ControlMode.PercentOutput, self.conveyor_speed)

        self.outtake_left.set(ctre.ControlMode.PercentOutput, self.outtake_speed)
        self.outtake_right.set(ctre.ControlMode.PercentOutput, -self.outtake_speed)

        self.intake.set(ctre.ControlMode.PercentOutput, self.intake_speed)

        self.pusher.set(self.pushing)
        self.grabber.set(self.grabbing)

    def read_auton_inputs(self):
        self.left_position = self.left.getSelectedSensorPosition()
        self.right_position = self.right.getSelectedSensorPosition()

        self.left_velocity = self.left.getSelectedSensorVelocity()
        self.right_velocity = self.right.getSelectedSensorVelocity()

        self.fused_heading = self.ahrs.getFusedHeading()
        self.yaw = deg(self.fused_heading)

    def read_tele_inputs(self):
        _update_xbox(self.driver, self.driver_xbox)
        _update_xbox(self.operator, self.operator_xbox)

        self.hall_effect_triggered = self.hall_effect.get()
        self.elevator_position = self.elevator.getSelectedSensorPosition()
        self.elevator_velocity = self.elevator.getSelectedSensorVelocity()

    def disable(self):
        self.left.set(ctre.ControlMode.Disabled, 0)
        self.right.set(ctre.ControlMode.Disabled, 0)
        self.elevator.set(ctre.ControlMode.Disabled, 0)
        self.conveyor_left.set(ctre.ControlMode.Disabled, 0)
        self.conveyor_right.set(ctre.ControlMode.Disabled, 0)
        self.outtake_left.set(ctre.ControlMode.Disabled, 0)
        self.outtake_right.set(ctre.ControlMode.Disabled, 0)
        self.intake.set(ctre.ControlMode.Disabled, 0)

        self.pusher.set(False)
        self.grabber.set(False)

    def reset_drive_position(self, positionRadians: float):
        pass

    def reset_elevator_position(self, positionRadians: float):
        pass

    def initialize(self):
        pass

    def enable(self):
        pass
