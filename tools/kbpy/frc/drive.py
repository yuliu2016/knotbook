from robot_io import *
from mathlib import *


class Drive:
    old_wheel = 0
    quickstop_accumulator = 0

    def __init__(self, io: IO):
        self.io = io

    def cheesy_drive(self, x_speed, z_rotation, quick_turn):

        neg_inertia = z_rotation - self.old_wheel
        self.old_wheel = z_rotation
        z_rotation = sin_scale(z_rotation, 0.8, passes=3)

        if z_rotation * neg_inertia > 0:
            neg_inertia_scalar = 2.5
        else:
            if abs(z_rotation) > .65:
                neg_inertia_scalar = 5
            else:
                neg_inertia_scalar = 3

        neg_inertia_accumulator = neg_inertia * neg_inertia_scalar

        z_rotation += neg_inertia_accumulator

        if quick_turn:
            if abs(x_speed) < 0.2:
                alpha = .1
                self.quickstop_accumulator = ((1 - alpha) * self.quickstop_accumulator +
                                              alpha * limit(z_rotation, 1.0) * 5)
            over_power = 1
            angular_power = z_rotation * .75
        else:
            over_power = 0
            angular_power = abs(x_speed) * z_rotation * self.sensitivity - self.quickstop_accumulator
            self.quickstop_accumulator = util.wrap_accumulator(self.quickstop_accumulator)

        right = left = x_speed

        left += angular_power
        right -= angular_power

        if left > 1:
            right -= over_power * (left - 1)
            left = 1
        elif right > 1:
            left -= over_power * (right - 1)
            right = 1
        elif left < -1:
            right += over_power * (-1 - left)
            left = -1
        elif right < -1:
            left += over_power * (-1 - right)
            right = -1

        self.io.left_demand = left
        self.io.right_demand = right
