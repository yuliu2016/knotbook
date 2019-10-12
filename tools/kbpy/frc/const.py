import ctre
import enum

from mathlib import INCH_TO_METRES, FEET_TO_METRES

# =====================================================
# The following motor controllers must have the same id
# as the the port they are plugged in to the PDP.
# Right pairs should be made symmetrical on the PDP.
# =====================================================

ELEVATOR_MASTER = 0
ELEVATOR_FOLLOWER = 15

DRIVE_LEFT = 1
DRIVE_RIGHT = 14

LEFT_FOLLOWER_A = 2
RIGHT_FOLLOWER_A = 13

LEFT_FOLLOWER_B = 3
RIGHT_FOLLOWER_B = 12

OUTTAKE_LEFT = 4
OUTTAKE_RIGHT = 11

CONVEYOR_LEFT = 5
CONVEYOR_RIGHT = 10

INTAKE = 9

# solenoids

PUSHER_SOLENOID = 2
GRABBER_SOLENOID = 1

# others

ELEVATOR_HALL_EFFECT = 9

# Drive
TICKS_PER_RADIAN = 0  # TODO

# Elevator
HATCH_1_HEIGHT = 0  # TODO
ELEVATOR_HOME_HEIGHT = 0  # todo
CARGO_2_HEIGHT = 0  # todo


# Enum classes

class LimelightMode(enum.IntEnum):
    DRIVER = 0
    VISION = 1


class GP(enum.IntEnum):
    CARGO = 0
    HATCH = 1
