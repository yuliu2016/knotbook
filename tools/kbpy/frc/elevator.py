from robot_io import *
from mathlib import *

from const import GP
import const

class Elevator:

    def __init__(self, io: IO):
        self.io = io
        self.manual_speed = 0
        self.is_manual = False
        self.setpoint_type = GP.HATCH
        self.setpoint = 0
        self.feedforward_on = True

    def increase_setpoint(self):
        pass

    def decrease_setpoint(self):
        pass

    def cool_setpoint(self):
        pass

    def update(self):
        pass