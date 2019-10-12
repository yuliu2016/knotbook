"""
Robot Math Library

Features (in order of implementation):

Common Constants
Function Utilities (eq, eq_zero, limit, deadband, sq, sq_sign, lerp, sin_scale)
Iteration Utilities (Latch, Delta, Counter, MinCycle, CircularBuffer)
PID Utilities (PID, PIDController)
Geometry Classes (translation, rotation, pose2d, twist2d, pose_k)
Polynomial Evaluation (cubic, quintic)
Spline Functions (bezier spline, hermite spline)
Pathing Utilities (path2d_point, polypath2d, path parameterizer)
Trajectory Generation (trajectory_state, generate_trajectory)
Motor Transmission Utilities (voltage_for_torque, torque_for_voltage)
Differential Drive Utilities (dd_chassis, dd_wheels)
Swerve Drive Utilities (swerve_chassis, swerve_wheels)
Path Followers (Pure Pursuit, Ramsete)

Code attribution:

FRC Team 865  (2019, 2014, 2015, FRC-Commons-Kotlin)
FRC Team 254  (2018, 2017, 2014, TrajectoryLib)
FRC Team 401  (2018, SnakeSkin)
FRC Team 1114 (2019, 2018)
FRC Team 6135 (2019, 2018, RobotPathfinder)
FRC Team 5190 (2019, FalconLib)
FRC Team 2539 (2019)
FRC Team 1323 (2018)
WPILib Suite (wpilibj, frc-docs)
JacisNonsence (Pathfinder)
RobotPy (PyFRC, robot characterization)
Michael Jansen (PathPlanner)
PythonRobotics
"""

import typing
import math
import collections

# unit conversions
FEET_TO_METRES = 0.3048
INCH_TO_METRES = 0.0254

# floating-point value constants
EPSILON = 1E-12
POSITIVE_INFINITY = math.inf
NEGATIVE_INFINITY = -math.inf

# Spline parameterization constants
# the parameterizer will try to fit each pair of points
# so that the forward distance is no more than MAX_DX,
# the lateral distance is no more than MAX_DY, and the
# heading turn is no more than MAX_DTHETA
MAX_DX = 0.1  # m
MAX_DY = 0.01  # m
MAX_DTHETA = 0.1  # rad


def eq(a, b, epsilon=EPSILON):
    """
    Check if a and b are "close enough" by a difference of epsilon
    :param a:
    :param b:
    :param epsilon:
    :return:
    """
    return a - epsilon <= b <= a + epsilon


def eq_zero(a):
    """
    Check if a value is close enough to 0
    :param a:
    :return:
    """
    return eq(a, 0.0)


def sq(x):
    """
    Squares a number -- Sometimes more accurate than raising power to the 2nd
    :param x:
    :return
    """
    return x * x


def sq_sign(x):
    """
    Squares the magnitude but keeps the sign
    :param x:
    :return:
    """
    return math.copysign(x * x, x)


def limit(value, max_value):
    """
    Limits a value within a range
    :param value:
    :param max_value:
    :return:
    """
    if value > max_value:
        return max_value
    if value < -max_value:
        return -max_value
    return value


def wrap_accumulator(acc):
    """
    Wraps accumulator!!!
    :param acc:
    :return:
    """
    if acc > 1:
        acc -= 1
    elif acc < -1:
        acc += 1
    else:
        acc = 0
    return acc


def sin_scale(val, non_linearity, passes):
    """
    recursive sin scaling! :D
    :param val: input
    :param non_linearity:
    :param passes: how many times to recurse
    :return: scaled val
    """
    scaled = sin(pi / 2 * non_linearity * val) / sin(pi / 2 * non_linearity)
    if passes == 1:
        return scaled
    else:
        return sin_scale(scaled, non_linearity, passes - 1)


def lerp(a, b, x):
    """
    Linearly interpolates between a and b
    :param a:
    :param b:
    :param x:
    :return:
    """
    return a + (b - a) * limit(x, 1.0)


def deadband(v, max_v, db):
    """
    Applies dead band to a input value
    :param v:
    :param max_v:
    :param db:
    :return:
    """
    v = limit(v, max_v)
    if abs(v) > db:
        if v > 0:
            return (v - db) / (max_v - db)
        return (v + db) / (max_v - db)
    return 0.0


def make_deadband_func(max_value, db):
    """
    Creates a deadband factory
    :param max_value:
    :param db:
    :return:
    """
    return lambda x: deadband(x, max_value, db)


class Latch:
    """
    Defines a latched boolean value, where update only returns True
    if the updated value changes from False to True
    """
    value = False

    def update(self, new_val: bool):
        r = new_val and not self.value
        self.value = new_val
        return r


class Delta:
    """
    Defines a delta object, which an keep track of how much a number changes
    """
    value = 0.0

    def update(self, new_val: float):
        old_val = self.value
        self.value = new_val
        return new_val - old_val


class Counter:
    """
    Creates a cycle counter, which returns True every n cycles
    """

    def __init__(self, cycles: int = 1):
        # this is so that the counter gets triggered the first time
        self.count = cycles
        self.cycles = cycles

    def get(self):
        self.count += 1
        if self.count >= self.cycles:
            self.count = 0
            return True
        return False


class MinCycle:
    """
    A boolean object that only returns True if the value has remained True
    for a minimum number of cycles
    """
    value = False
    count = 0

    def __init__(self, cycles: int = 1):
        # this is so that the counter gets triggered the first time
        self.min_cycles = cycles

    def update(self, new_val: bool):
        if new_val:
            self.count += 1
        else:
            self.count = 0
        return self.count >= self.min_cycles


class CircularBuffer(collections.deque):
    """
    A circular buffer
    """

    def __init__(self, size=0):
        super().__init__(maxlen=size)

    @property
    def average(self):
        try:
            return sum(self) / len(self)
        except ZeroDivisionError:
            return 0


class PID(typing.NamedTuple):
    """
    PID (and F) values
    """
    kP: float = 0.0
    kI: float = 0.0
    kD: float = 0.0
    kF: float = 0.0


class PIDController:
    """
    A simple PID-F controller that can update by error or by setpoint
    """

    last_error = 0.0
    d_error = 0.0
    sum_error = 0.0
    time_in_epsilon = 0.0
    setpoint = 0.0
    dt = 0.0

    def __init__(self, pid: "PID",
                 error_epsilon: float = 0.1,
                 d_error_epsilon: float = 0.1,
                 min_time_in_epsilon: float = 0.1,
                 dt_normalizer: float = 50.0,
                 max_output: float = 1.0):

        self.pid = pid
        self.error_epsilon = error_epsilon
        self.d_error_epsilon = d_error_epsilon
        self.min_time_in_epsilon = min_time_in_epsilon
        self.dt_normalizer = dt_normalizer
        self.max_output = max_output

    def update_by_error(self, error: float, dt: float = -1):

        if dt > 0:
            self.dt = dt

        # normalize the change in time so kD doesn't need to be too high
        # and kI doesn't need to be too low
        normalized_dt = self.dt * self.dt_normalizer

        # calculate proportional gain
        p_gain = self.pid.kP * error

        # calculate conditions for resetting integral sum

        # error is bigger than kP can handle
        if ((not eq(p_gain, 0.0, self.max_output))

                # error is smaller than the epsilon range
                or error.epsilonEquals(0.0, errorEpsilon)

                # sumError is in reverse while kP goes in forward
                or (p_gain > 0 > self.sum_error)

                # sumError is in forward while kP goes in reverse
                or (p_gain < 0 < self.sum_error)
        ):
            self.sum_error = 0.0
        else:
            # otherwise add current error to the sum of errors
            self.sum_error += error * normalized_dt

        # calculate the integral gain
        i_gain = self.pid.kI * self.sum_error

        # calculate change in error
        self.d_error = (error - self.last_error) / normalized_dt
        self.last_error = error

        # calculate derivative gain
        d_gain = self.pid.kD * dError

        # calculate the time when error and change in error is small enough
        # by adding the unmodified dt to a sum
        if eq(error, 0.0, self.error_epsilon) \
                and eq(self.d_error, 0.0, self.d_error_epsilon):
            self.time_in_epsilon += dt
        else:
            # reset time_in_epsilon
            self.time_in_epsilon = 0.0

        return limit(p_gain + i_gain + d_gain, self.max_output)

    def updateBySetpoint(self, actual: float):
        # calculate feedforward gain
        f_gain = self.pid.kF * setpoint

        # calculate error
        error = setpoint - actual

        # calculate feedback gains
        pid_gain = updateByError(error)

        # add up gains to get the output
        return limit(f_gain + pid_gain, self.max_output)

    def isDone(self):
        return self.timeInEpsilon > self.minTimeInEpsilon


class translation(typing.NamedTuple):
    """
    Represents a 2D translation transformation
    (same as adding complex numbers)
    """
    x: float
    y: float

    def __str__(self):
        return f"T(x={self.x:.2f}, y={self.y:.2f})"

    def __repr__(self):
        return f"translation(x={self.x:.4f}, y={self.y:.4f})"

    def __add__(self, other) -> "translation":
        """
        compound transformation (translate by another translation)
        :param other:
        :return:
        """
        return translation(self.x + other.x, self.y + other.y)

    def __neg__(self):
        """
        Invert the translation
        :return:
        """
        return translation(-self.x, -self.y)

    def __sub__(self, other: "translation"):
        return translation(self.x - other.x, self.y - other.y)

    def __abs__(self):
        """
        :return: Magnitude of translation
        """
        return math.hypot(self.x, self.y)

    def __mul__(self, other):
        return translation(self.x * other, self.y * other)

    def __truediv__(self, other):
        return self * (1 / other)

    def __complex__(self):
        return complex(self.x, self.y)

    def eq(self, other: "translation", epsilon=EPSILON):
        return eq(self.x, other.x, epsilon) and eq(self.y, other.y, epsilon)

    def to_rotation(self):
        return rotation(self.x, self.y)

    def rotate(self, other: "rotation"):
        """
        Rotats translation by a certain rotation
        :param other:
        :return:
        """
        return translation(
            x=self.x * other.cos - self.y * other.sin,
            y=self.x * other.sin + self.y * other.cos
        )

    def transpose(self):
        return translation(self.y, self.x)

    def flip_x(self):
        return translation(-self.x, self.y)

    def flip_y(self):
        return translation(self.x, -self.y)

    def dot(self, other: "translation"):
        return self.x * other.x + self.y * other.y

    def cross(self, other: "translation"):
        return self.x * other.y - self.y * other.x

    def normalize(self):
        return self * (1 / abs(self))

    def distance_to(self, other: "translation"):
        """
        Compute the distance to another translation
        :param other:
        :return:
        """
        return abs(other - self)


def fit_parabola(p1: "translation", p2: "translation", p3: "translation"):
    """
    Fits a parabola that lies on three points
    """
    a = p3.x * (p2.y - p1.y) + p2.x * (p1.y - p3.y) + p1.x * (p3.y - p2.y)

    b = p3.x * p3.x * (p1.y - p2.y) + p2.x * p2.x * \
        (p3.y - p1.y) + p1.x * p1.x * (p2.y - p3.y)

    return -b / (2 * a)


class rotation(typing.NamedTuple):
    """
    Represents a 2D rotation transformation
    (same as multiplying complex numbers)
    """
    cos: float
    sin: float

    def __add__(self, other) -> "rotation":
        """
        compound transformation (rotate by another rotation)
        :param other:
        :return:
        """
        return rotation(
            cos=self.cos * other.cos - self.sin * other.sin,
            sin=self.cos * other.sin + self.sin * other.cos
        )

    def __neg__(self):
        return rotation(self.cos, -self.sin)

    def __abs__(self):
        return math.hypot(self.cos, self.sin)

    def __sub__(self, other) -> "rotation":
        return self + rotation(self.cos, -self.sin)

    def __mul__(self, other) -> "rotation":
        return rotation(self.cos * other, self.sin * other)

    def __complex__(self):
        return complex(self.cos, self.sin)

    def eq(self, other: "rotation", epsilon=EPSILON):
        return eq(self.cos, other.cos, epsilon) \
               and eq(self.sin, other.sin, epsilon)

    def to_translation(self, scale=1.0):
        return translation(self.cos * scale, self.sin * scale)

    def to_radians(self):
        return math.atan2(self.sin, self.cos)

    def to_degrees(self):
        return math.degrees(self.to_radians())

    def distance_to(self, other: "rotation"):
        return (other - self).to_radians()

    def tan(self):
        """
        Returns the tangent value (sin/cos) of the rotation
        :return:
        """
        if abs(self.cos) < 1E-12:
            if self.sin >= 0:
                return POSITIVE_INFINITY
            return NEGATIVE_INFINITY
        return self.sin / self.cos

    def normal(self):
        """
        Returns the normal (90deg) rotation
        :return:
        """
        return rotation(-self.sin, self.cos)

    def normalize(self) -> "rotation":
        return self * (1 / abs(self))

    def parallel_to(self, other: "rotation"):
        """
        Check if the other rotation is parallel to this
        :param other:
        :return:
        """
        return eq_zero(self.to_translation()
                       .cross(other.to_translation()))


def rad(x):
    """
    Create a rotation from radians
    :param x:
    :return:
    """
    return rotation(math.cos(x), math.sin(x))


def deg(x):
    """
    Create a rotation from degrees
    :param x:
    :return:
    """
    return rad(math.radians(x))


class pose2d(typing.NamedTuple):
    """
    Robot pose (rigid transform)
    """
    position: translation
    heading: rotation

    def __sub__(self, other: "pose2d") -> "pose2d":
        return self + other.inverse()

    def __neg__(self):
        return self.inverse()

    def __add__(self, other: "pose2d") -> "pose2d":
        return pose2d(self.position + other.position.rotate(other.heading),
                      self.heading + other.heading)

    def eq(self, other: "pose2d", epsilon=EPSILON):
        return self.position.eq(other.position, epsilon) and self.heading.eq(other.heading, epsilon)

    def inverse(self):
        return pose2d(-self.position.rotate(-self.heading), -self.heading)

    def log(self):
        return pose2d_log(self)

    def distance_to(self, other: "pose2d"):
        return abs((other - self).log())

    def is_colinear(self, other: "pose2d"):
        if not self.heading.parallel_to(other.heading):
            return False
        pose_delta = (other - this).log()
        return eq_zero(pose_delta.dy) and eq_zero(pose_delta.dtheta)

    def mirror(self):
        return pose2d(self.position.flip_y(), -self.heading)

    def intersection(self, other: "pose2d"):
        return pose2d_intersection(self, other)


def pose2d_log(a: "pose2d"):
    """
   Converts this pose to a delta between the origin and this pose
   :return:
   """

    d_theta = a.heading.to_radians()
    half_theta = 0.5 * d_theta
    cos_minus_one = a.heading.cos - 1.0

    if abs(cos_minus_one) < 1E-9:
        half_theta_by_tan_of_half_dtheta = 1.0 - 1.0 / 12.0 * dTheta * dTheta
    else:
        half_theta_by_tan_of_half_dtheta = -(halfTheta * rotation.sin) / cos_minus_one

    t = a.position.rotate(rotation(half_theta_by_tan_of_half_dtheta, -half_theta))
    return twist2d(t.x, t.y, d_theta)


def pose2d_intersection(a: "pose2d", b: "pose2d"):
    """
    Intersection between two poses
    :param a: 
    :param b: 
    :return: 
    """
    b_heading = b.heading

    if a.heading.parallel_to(b_heading):
        # Lines are parallel.
        return translation(POSITIVE_INFINITY, POSITIVE_INFINITY)

    if abs(a.heading.cos) < abs(b_heading.cos):
        return pose2d_intersect_internal(this, b)
    else:
        return pose2d_intersect_internal(b, this)


def pose2d_intersect_internal(a: "pose2d", b: "pose2d"):
    """
    Helper method
    :param a: 
    :param b: 
    :return: 
    """
    ar = a.heading
    br = b.heading
    at = a.position
    bt = b.position

    tan_b = br.tan()

    t = ((at.x - bt.x) * tan_b + bt.y - at.y) / (ar.sin - ar.cos * tan_b)

    if math.isnan(t):
        return Translation2D(POSITIVE_INFINITY, POSITIVE_INFINITY)
    else:
        return at + ar.translation * t


class twist2d(typing.NamedTuple):
    """
    Represents a delta between two poses
    """
    dx: float
    dy: float
    dtheta: float

    def __abs__(self):
        return math.hypot(self.dx, self.dy)

    def __mul__(self, other) -> "twist2d":
        return twist2d(self.dx * other, self.dy * other, self.dtheta * other)

    def exp(self) -> "pose2d":
        return twist2d_exp(self)


def twist2d_exp(twist: "twist2d"):
    """
    Converts a twist transformation into a pose transformation
    :param twist: 
    :return: 
    """
    sin_theta = math.sin(twist.dtheta)
    cos_theta = math.cos(twist.dtheta)

    if abs(dTheta) < 1E-9:
        s = 1.0 - 1.0 / 6.0 * twist.dtheta * twist.dtheta
        c = .5 * twist.dtheta
    else:
        s = sin_theta / twist.dtheta
        c = (1.0 - cos_theta) / twist.dtheta

    return pose2d(
        position=translation(twist.dx * s - twist.dy * c, twist.dx * c + twist.dy * s),
        heading=rotation(cos_theta, sin_theta)
    )


class pose_k(typing.NamedTuple):
    """
    Pose with curvature
    """
    pose: pose2d
    k: float
    dk_ds: float = 0.0


class BasePolynomial:
    """
    Base abstract class for a polynomial function
    """

    def fx(self, x: float) -> float:
        """
        Evaluate function
        :param x:
        :return:
        """
        pass

    def dx(self, x: float) -> float:
        """
        Evaluate first derivative
        :param x:
        :return:
        """
        pass

    def ddx(self, x: float) -> float:
        """
        Evaluate second derivative
        :param x:
        :return:
        """
        pass

    def dddx(self, x: float) -> float:
        """
        Evaluate third derivative
        :param x:
        :return:
        """
        pass


class cubic_polynomial(typing.NamedTuple, BasePolynomial):
    """
    Represents the coefficients of a cubic polynomial
    """

    a: float
    b: float
    c: float
    d: float

    def fx(self, x):
        """
        Evaluate polynomial
        :param x:
        :return:
        """
        return (self.a * x ** 3 +
                self.b * x ** 2 +
                self.c * x +
                self.d)

    def dx(self, x):
        """
        Evaluate first derivative
        :param x:
        :return:
        """
        return (3 * self.a * x ** 2 +
                2 * self.b * x +
                self.c)

    def ddx(self, x):
        """
        Evaluate second derivative
        :param x:
        :return:
        """
        return (6 * self.a * x +
                2 * self.b)

    def dddx(self, x):
        """
        Evaluate third derivative
        :return:
        """
        return 6 * self.a


def cubic_hermite_spline(
        p0: float,
        m0: float,
        p1: float,
        m1: float
):
    """
    Create a single cubic hermite spline functions
    :param p0:
    :param m0:
    :param p1:
    :param m1:
    :return: a cubic polynomial function that represents the hermite spline
    """
    return cubic_polynomial(
        a=2 * p0 - 2 * p1 + m0 + m1,
        b=-2 * m0 - m1 - 3 * p0 + 3 * p1,
        c=m0,
        d=p0
    )


def cubic_bezier_spline(
        p0: float,
        p1: float,
        p2: float,
        p3: float
):
    """
    Create a sigle cubic bezier spline
    :param p0: starting point
    :param p1: control point 1
    :param p2: control point 2
    :param p3: end point
    :return: a cubic polynomial function that represents the bezier spline
    """
    return cubic_polynomial(
        a=-1 * p0 + 3 * p1 - 3 * p2 + p3,
        b=+3 * p0 - 6 * p1 + 3 * p2,
        c=-3 * p0 + 3 * p1,
        d=p0
    )


class quintic_polynomial(typing.NamedTuple, BasePolynomial):
    """
    Represents the coefficients of a quintic polynomial
    """

    a: float
    b: float
    c: float
    d: float
    e: float
    f: float

    def fx(self, x):
        """
        Evaluate polynomial
        :param x:
        :return:
        """
        return (self.a * x ** 5 +
                self.b * x ** 4 +
                self.c * x ** 3 +
                self.d * x ** 2 +
                self.e * x +
                self.f)

    def dx(self, x):
        """
        Evaluate first derivative
        :param x:
        :return:
        """
        return (5 * self.a * x ** 4 +
                4 * self.b * x ** 3 +
                3 * self.c * x ** 2 +
                2 * self.d * x +
                self.e)

    def ddx(self, x):
        """
        Evaluate second derivative
        :param x:
        :return:
        """
        return (20 * self.a * x ** 3 +
                12 * self.b * x ** 2 +
                6 * self.c * x +
                2 * self.d)

    def dddx(self, x):
        """
        Evaluate third derivative
        :param x:
        :return:
        """
        return (60 * self.a * x ** 2 +
                24 * self.b * x +
                6 * self.c)


def quintic_hermite_spline(
        p0: float,
        m0: float,
        dm0: float,
        p1: float,
        m1: float,
        dm1: float
):
    """
    Create a single quintic hermite spline
    :param p0:
    :param m0:
    :param dm0:
    :param p1:
    :param m1:
    :param dm1:
    :return: a quintic polynomial function that represents the hermite spline
    """
    return quintic_polynomial(
        a=-6 * p0 - 3 * m0 - 0.5 * dm0 + 0.5 * dm1 - 3 * m1 + 6 * p1,
        b=15 * p0 + 8 * m0 + 1.5 * dm0 - dm1 + 7 * m1 - 15 * p1,
        c=-10 * p0 - 6 * m0 - 1.5 * dm0 + 0.5 * dm1 - 4 * m1 + 10 * p1,
        d=0.5 * dm0,
        e=m0,
        f=p0
    )


class path2d_point(typing.NamedTuple):
    """
    An instantaneous point on a continous path
    """
    t: float
    fx: float
    fy: float
    dx: float
    dy: float
    ddx: float
    ddy: float
    dddx: float
    dddy: float

    def curvature(self):
        """
        Curvature of path point
        :return:
        """
        return ((self.dx * self.ddy - self.ddx * self.dy) /
                (sq(self.dx) + sq(self.dy)) ** 1.5)

    def d_curvature(self):
        """
        Derivative of the curvature
        :return:
        """
        dx2dy2 = sq(self.dx) + sq(self.dy)
        num = ((self.dx * self.dddy -
                self.dddx * self.dy) * dx2dy2 -
               3.0 * (self.dx * self.ddy - self.ddx * self.dy) *
               (self.dx * self.ddx + self.dy * self.ddy))
        return num / dx2dy2 ** 2.5

    def d_curvature_2(self):
        """
        Square of the change in curvature
        :return:
        """
        return sq(self.d_curvature())

    def d_curvature_ds(self):
        """
        Change in curvature with respect to change in position
        :return:
        """
        return self.d_curvature() / math.hypot(self.dx, self.dy)

    def position(self):
        """
        Position of path point
        :return:
        """
        return translation(self.fx, self.fy)

    def heading(self):
        """
        Heading of path point
        :return:
        """
        mag = math.hypot(self.dx, self.dy)
        return rotation(self.dx / mag, self.dy / mag)

    def to_pose(self):
        """
        Pose of path point
        :return:
        """
        return pose2d(self.position(), self.heading())

    def to_pose_k(self):
        """
        Pose with curvature
        :return:
        """
        return pose_k(
            self.to_pose(),
            self.curvature(),
            self.d_curvature_ds()
        )


class polypath2d(typing.NamedTuple):
    """
    A continous 2D path represented by two polynomials
    """
    x: BasePolynomial
    y: BasePolynomial

    def __getitem__(self, t: float) -> "path2d_point":
        """
        Gets a point on the path
        :param t:
        :return:
        """
        assert 0.0 <= t <= 1.0
        return path2d_point(
            t=t,
            fx=self.x.fx(t),
            fy=self.y.fx(t),
            dx=self.x.dx(t),
            dy=self.y.dx(t),
            ddx=self.x.ddx(t),
            ddy=self.y.ddx(t),
            dddx=self.x.dddx(t),
            dddy=self.y.dddx(t)
        )

    def start_point(self):
        return self[0.0]

    def end_point(self):
        return self[1.0]


def paths_sum_d_curvature(paths: typing.List[polypath2d]):
    """
    Returns the total sum of changes of curvature squared
    :param paths:
    :return:
    """
    s = 0.0
    for path in paths:
        for i in range(100):
            s += path[i / 100.0].d_curvature_2()
    return s


def quintic_spline_from_pose(
        start: "pose2d",
        end: "pose2d",
        bend_factor: "float" = 1.2
):
    """
    Create a quintic spline between two poses
    :param start:
    :param end:
    :param bend_factor:
    :return:
    """
    scale = start.position.distance_to(end.position) * bend_factor

    return polypath2d(
        x=quintic_hermite_spline(
            p0=start.position.x,
            p1=end.position.x,
            m0=start.heading.cos * scale,
            m1=end.heading.cos * scale,
            dm0=0.0,
            dm1=0.0
        ),
        y=quintic_hermite_spline(
            p0=start.position.y,
            p1=end.position.y,
            m0=start.heading.sin * scale,
            m1=end.heading.sin * scale,
            dm0=0.0,
            dm1=0.0
        )
    )


def quintic_splines(
        *wp: "pose2d",
        bend_factor=1.2
):
    """
    Create a sequence of quintic splines
    :param wp: waypoints
    :param bend_factor:
    :return:
    """
    return [quintic_spline_from_pose(wp[i], wp[i + 1], bend_factor)
            for i in range(len(wp) - 1)]


def parameterize_segments(segments: typing.List[polypath2d]):
    """
    Parameterize a list of path segments
    :param segments:
    :return:
    """
    first_point = segments[0].start_point().to_pose_k()
    points = [first_point]
    for segment in segments:
        points.extend(parameterize_path2d(segment))
    return points


def parameterize_path2d(path: polypath2d):
    """
    Parameterize a single polypath
    :param path:
    :return:
    """
    points = []
    parameterize_recursive(path, points, t0=0.0, t1=1.0)
    return points


def parameterize_recursive(
        path: polypath2d,
        points: typing.List[pose_k],
        t0: float,
        t1: float
):
    """
    Recursively parameterize a spline
    :param path:
    :param points:
    :param t0:
    :param t1:
    :return:
    """
    p0 = path[t0]
    p1 = path[t1]
    h0 = p0.heading()
    h1 = p1.heading()
    pt0 = p0.position()
    pt1 = p1.position()

    # get the twist transformation between start and and points
    twist = pose2d((pt1 - pt0).rotate(-h0), h1 - h0).log()

    if twist.dy > MAX_DY or twist.dx > MAX_DX or twist.dtheta > MAX_DTHETA:
        parameterize_recursive(path, points, t0, (t0 + t1) / 2.0)
        parameterize_recursive(path, points, (t0 + t1) / 2.0, t1)
    else:
        points.append(p1.to_pose_k())


class trajectory_state:
    """
    Defines the state of the robot in a trajectory

    This doesn't use ChassisState because NamedTuples are immutable
    """
    pose_k: pose_k

    v: float = 0.0
    w: float = 0.0

    dv: float = 0.0
    dw: float = 0.0

    ddv: float = 0.0
    ddw: float = 0.0

    t: float = 0.0

    def __init__(self, p: "pose_k"):
        self.pose_k = p


def generate_trajectory(
        path: typing.List[pose_k],  # [(((x, y), θ), k, dk_ds)]
        wheelbase_radius: float,  # m
        max_vel: float,  # m/s
        max_acc: float,  # m/s^2
        max_centripetal_acc: float,  # s^-1
        max_jerk: float  # m/s^3
):
    """
    Generates a list of timed trajector_state for a differential drive robot,
    based on a list of pose_k and drive train parameters

    The path may contain points of infinite curvature to indicate turning
    in place. In such cases, the function assumes that the translation
    field stays the same. The direction of turning is determined by the
    sign of the curvature field

    ### Trajectory Generation Steps
    1. Find the arc length between every consecutive point
    2. Perform a forward pass states to satisfy isolated and positive acceleration constraints
    3. Perform a reverse pass to satisfy negative acceleration constraints
    4. Perform an accumulative pass to calculate higher-order derivatives of velocity
    5. Apply jerk limiting
    6. Integrate dt of each state into total trajectory time

    Complexity based on path size: O(n)

    [generateTrajectory] is a pure function

    :param path: path the target path of the trajectory.

    :param wheelbase_radius: wheelbaseRadius the effect wheel base radius in metres

    :param max_vel: maxVelocity the maximum linear velocity allowed in this trajectory in metres/second. It is
    better to set this value to below the actual maximum of the robot for efficiency and accuracy.
    (At most 80% of actual max velocity)

    :param max_acc: the maximum linear acceleration allowed in this trajectory in metres/second^2
    Lower this for robot stability. However, this would usually increase total trajectory time more
    significantly than [maxVelocity]

    :param max_centripetal_acc: the maximum centripetal acceleration in hertz. This value
    can reduce maximum velocity at high curvatures and is useful to prevent tipping when the CoG is high

    :param max_jerk: the maximum linear jerk allowed in this trajectory in metres/second^3. This value can
    reduce spikes in voltages on the drive train to increase stability. [Double.POSITIVE_INFINITY] may
    be passed instead to disable jerk limiting

    :return: a list of timed trajectory points
    """
    max_angular_vel = max_vel / wheelbase_radius  # rad / s
    max_angular_acc = max_acc / wheelbase_radius  # rad / s ^ 2

    states = [trajectory_state(p) for p in path]

    arc_lens = tg_compute_arc_len(path)

    tg_forward_pass(states, arc_lens, wheelbase_radius, max_vel, max_acc,
                    max_angular_vel, max_angular_acc, max_centripetal_acc)

    tg_reverse_pass(states, arc_lens, max_acc, max_angular_acc)

    tg_accum_pass(states)

    if math.isinf(max_jerk):
        tg_ramped_acc_pass(states, arc_lens, max_jerk)

    tg_integration_pass(states)

    return states


def tg_compute_arc_len(path: typing.List[pose_k]):
    """
    Compute arc length between each pair of poses in the path
    :param path:
    :return:
    """
    arc_lens: typing.List[float] = []

    for index in range(len(path) - 1):
        cur = path[index]
        nxt = path[index + 1]

        if cur.pose.eq(nxt.pose):
            raise Exception("Trajectory Generator - "
                            "Two consecutive points contain the same pose")

        # Get the magnitude of curvature on the next state
        # Note that the curvature is taken from the `next` state to predict
        # changing from and to infinity so that it doesn't result in a very
        # small arc length
        k = abs(nxt.k)

        if math.isinf(k):
            # Robot is turning in place, arcLength = 0.0
            # Returns the magnitude of the angular distance in radians
            arc_len = abs((nxt.pose.heading - cur.pose.heading).to_radians())
        else:
            # Robot is moving in a curve or straight line
            # Returns the linear distance in meters

            # Get the chord length (translational distance)
            distance = cur.pose.position.distance_to(nxt.pose.position)

            if distance == 0:
                raise Exception("Trajectory Generator - "
                                "Overlapping points without infinite curvature")

            if k < 1E-6:
                # Going straight, arcLength = distance
                arc_len = distance
            else:
                # Moving on a radius:
                # arcLength = theta * r = theta / k
                # theta = asin(half_chord / r) * 2 = asin(half_chord * k) * 2
                # arcLength = asin(half_chord * k) * 2 / k
                arc_len = math.asin((distance / 2) * k) * 2 / k

        arc_lens.append(arc_len)


def tg_forward_pass(
        states: typing.List[trajectory_state],
        arc_lens: typing.List[float],
        wheelbase_radius: float,
        max_vel: float,
        max_acc: float,
        max_angular_vel: float,
        max_angular_acc: float,
        max_centripetal_acc: float
):
    """
    Forward pass
    :param states:
    :param arc_lens:
    :param wheelbase_radius:
    :param max_vel:
    :param max_acc:
    :param max_angular_vel:
    :param max_angular_acc:
    :param max_centripetal_acc:
    :return:
    """
    states[0].v = 0.0
    states[0].w = 0.0

    for i in range(len(states)):

        arc_len = arc_lens[i]
        cur = states[i]
        nxt = states[i + 1]

        k = abs(nxt.pose_k.k)

        if math.isinf(k):
            # Robot is turning in place; using angular values instead
            max_reachable_angular_vel = sqrt(sq(cur.w) + 2 * max_angular_acc * arc_len)
            nxt.w = min(max_angular_vel, max_reachable_angular_vel)

            # Make sure that the linear velocity is taken care of
            nxt.v = 0.0
            nxt.t = (2 * arcLength) / (cur.w + nxt.w)
        else:
            # Robot is moving in a curve or straight line

            # Velocity constrained by curvature equations
            # eqn 1. w = (right - left) / (2 * L)
            # eqn 2. v = (left + right) / 2
            #
            # 1. Rearrange equation 1:
            #        w(2 * L) = right - left;
            #        left = right - w(2 * L);
            # 2. Assuming the right side is at max velocity:
            #        right = V_max;
            #        left = V_max - w(2 * L)
            # 3. Substitute left and right into equation 2:
            #        v = (2 * V_max - w(2 * L)) / 2
            # 5. Substitute w = v * k into equation 2:
            #        v = (2 * V_max-v * k * 2 * L) / 2
            # 6. Rearrange to solve:
            #        v = V_max - v * k * L;
            #        v + v * k * L = V_max;
            #        v * (1 + k * L) = V_max;
            #        v = V_max / (1 + k * L);
            drive_kinematic_constraint = max_vel / (1 + k * wheelbase_radius)

            # Velocity constrained inversely proportional to the curvature to slow down around turns
            if k > 1E-6:
                centripetal_acc_constraint = max_centripetal_acc / k
            else:
                centripetal_acc_constraint = max_vel

            # Find the total constrained velocity
            constrained_vel = min(drive_kinematic_constraint, centripetal_acc_constraint)

            # Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
            max_reachable_vel = math.sqrt(sq(cur.v) + 2 * max_acc * arc_len)

            # Limit velocity based on curvature constraint and forward acceleration
            nxt.v = min(max_vel, constrained_vel, max_reachable_vel)

            # Make sure that the angular velocity is taken care of
            nxt.w = nxt.v * k

            # Calculate the forward dt
            nxt.t = (2 * arc_len) / (cur.v + nxt.v)


def tg_reverse_pass(
        states: typing.List[trajectory_state],
        arc_lens: typing.List[float],
        max_acc: float,
        max_angular_acc: float
):
    """
    Reverse pass
    :param states:
    :param arc_lens:
    :param max_acc:
    :param max_angular_acc:
    :return:
    """
    # Assign the final linear and angular velocity
    states[-1].v = 0.0
    states[-1].w = 0.0

    for i in range(states.size - 1, 1, -1):

        arc_len = arc_lens[i - 1]
        cur = states[i]
        nxt = states[i - 1]

        k = abs(cur.pose_k.k)

        if math.isinf(k):
            # Robot is turning in place; using angular values instead
            max_reachable_angular_vel = sqrt(sq(cur.w) + 2 * max_angular_acc * arc_len)
            nxt.w = min(nxt.w, max_reachable_angular_vel)
            nxt.v = 0.0
            cur.t = max(cur.t, (2 * arc_len) / (cur.w + nxt.w))

        else:
            # Robot is moving in a curve or straight line

            # Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
            max_reachable_vel = sqrt(sq(cur.v) + 2 * max_acc * arc_len)

            # Limit velocity based on reverse acceleration
            nxt.v = min(nxt.v, max_reachable_vel)
            nxt.w = nxt.v * k

            # Calculate the reverse dt
            cur.t = max(cur.t, (2 * arc_len) / (cur.v + nxt.v))


def tg_accum_pass(
        states: typing.List[trajectory_state]
):
    """
     Accumulative Pass for calculating higher-order derivatives
     (acceleration and jerk), as well as giving back the sign
     of the curvature
    :param states:
    :return:
    """

    for i in range(len(states)):
        cur = states[i + 1]
        last = states[i]

        # Calculate acceleration
        cur.dv = (cur.v - last.v) / cur.t

        k = cur.pose_k.k

        # Calculate angular velocity
        if math.isinf(k):
            # Correct the sign of angular velocity
            cur.w = cur.w.withSign(k)
        else:
            # Multiply curvature by the linear velocity
            cur.w = cur.v * k

        # Calculate angular acceleration
        cur.dw = (cur.w - last.w) / cur.t

        # Calculate jerk
        cur.ddv = (cur.dv - last.dv) / cur.t
        cur.ddw = (cur.dw - last.dw) / cur.t


# noinspection PyUnusedLocal
def tg_ramped_acc_pass(
        states: typing.List[trajectory_state],
        arc_lens: typing.List[float],
        max_jerk: float
):
    """
    Reduces jerk
    :param states:
    :param arc_lens:
    :param max_jerk:
    :return:
    """

    # Find a list of points that exceeds the max jerk
    jerk_points = []

    for i in range(len(states)):

        # Limit jerk at each of these points
        if abs(states[i].ddv) > max_jerk:
            jerk_points.add(i)

    for i in range(len(jerk_points)):

        state_index = jerk_points[i]

        # Calculate a range of points to spread out the required acceleration
        acc_range = abs(states[state_index].ddv / (2 * maxJerk)).toInt() * 3 + 1

        # Calculate the bounds of the actual range with respect to other jerk points
        start = max(jerk_points[i - 1], state_index - acc_range)  # to do account for index bounds
        end = minOf(jerk_points[i + 1], state_index + acc_range)

        acc_start = states[start].dv
        acc_end = states[end].dv

        # Calculate the individual step size
        max_time = sum(a.t for a in states[start:end + 1])

        t = 0.0

        for j in range(start, end):
            nxt = states[j + 1]
            cur = states[j]

            t += cur.t
            interpolant = t / max_time

            # Interpolate the acceleration
            cur.dv = lerp(acc_start, acc_end, interpolant)

            # Apply kinematic equation vf^2 = vi^2 + 2ax, solve for vf
            arc_len = arc_lens[j]

            jerk_limited_vel = sqrt(cur.v.squared + 2 * cur.dv * arc_len)
            nxt.v = min(nxt.v, jerk_limited_vel)

            # Set the new dt
            nxt.t = max(nxt.t, 2 * arc_len) / abs(cur.v + nxt.v)


def tg_integration_pass(
        states: typing.List[trajectory_state]
):
    """
    Integrate timesteps
    :param states:
    :return:
    """
    for i in range(1, len(states)):
        states[i].t += states[i - 1].t

    first = states[0]
    first.dv = 0
    first.dw = 0
    first.ddv = 0
    first.ddw = 0

    last = states[-1]
    last.dv = 0
    last.dw = 0
    last.ddv = 0
    last.ddw = 0


class dd_chassis(typing.NamedTuple):
    """
    Define the chassis state of a differential drive
    """
    linear: float = 0.0
    angular: float = 0.0


class dd_wheels(typing.NamedTuple):
    """
    Define the wheel state of a differential drive
    """
    left: float = 0.0
    right: float = 0.0

    def __mul__(self, other):
        """
        Scales a drive command
        :param other:
        :return:
        """
        return dd_wheels(self.left * other, self.right * other)


def dd_chassis_for_wheels(wheels: dd_wheels, wheelbase_radius: float):
    """
    Solves the forward kinematics of the drive train by converting the
    speeds/accelerations of wheels on each side into linear and angular
    speed/acceleration

    Equations:
    v = (left + right) / 2
    w = (right - left) / L

    for velocity, solves (m/s, m/s) into (m/s, rad/s)
    for acceleration, solves (m/s^2, m/s^2) into (m/s^2, rad/s^2)

    :param wheels: the wheel state
    :param wheelbase_radius: the radius of the robot's wheel base
    :return: the chassis state
    """
    return dd_chassis(
        linear=(wheels.left + wheels.right) / 2.0,
        angular=(wheels.right - wheels.left) / (2 * wheelbase_radius)
    )


def dd_wheel_for_chassis(chassis: dd_chassis, wheelbase_radius: float):
    """
    Solves the inverse kinematics of the drive train by converting linear
    and angular speed/acceleration into the speeds/accelerations of wheels
    on each side

    for velocity, solves (m/s, rad/s) into (m/s, m/s)
    for acceleration, solves (m/s^2, rad/s^2) into (m/s^2, m/s^2)

    :param chassis: the chassis state
    :param wheelbase_radius: the radius of the robot's wheel base
    :return: the wheel state
    """
    return dd_wheels(
        left=chassis.linear - chassis.angular * wheelbase_radius,
        right=chassis.linear + chassis.angular * wheelbase_radius
    )


def torque_for_voltage():
    pass


def voltage_for_torque():
    pass


def free_speed_for_voltage():
    pass


def dd_torque_for_acc():
    pass


def dd_voltage_for_demand():
    pass


class swerve_wheels(typing.NamedTuple):
    """
    Define the wheel state of a swerve drive
    """
    left_front: rotation
    right_front: rotation
    left_rear: rotation
    right_rear: rotation


class swerve_chassis(typing.NamedTuple):
    """
    Defines the chassis state of a swerve drive
    """
    motion: translation
    turn: rotation


def swerve_chassis_for_wheels():
    pass


def swerve_wheels_for_chassis():
    pass


def pp_find_radius():
    pass