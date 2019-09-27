import typing
import time


class Action:
    done = False

    def first_cycle(self):
        pass

    def update(self):
        pass

    def should_finish(self) -> bool:
        return True

    def last_cycle(self):
        pass

    def interrupt(self):
        pass


class SeriesAction(Action):

    def __init__(self, *actions: "Action"):
        self.queue = actions
        self.current: "typing.Optional[Action]" = None

    def first_cycle(self):
        self.update()

    def update(self):
        if self.current is None:
            if not self.queue:
                return
            self.current = self.queue.pop(0)
            self.current.first_cycle()
        self.current.update()
        if self.current.should_finish():
            self.current.last_cycle()
            self.current = None

    def should_finish(self) -> bool:
        return not self.queue and not self.current

    def last_cycle(self):
        pass

    def interrupt(self):
        self.current.interrupt()
        self.queue.clear()


class ParallelAction(Action):

    def __init__(self, *actions: Action):
        self.actions = actions

    def first_cycle(self):
        for action in self.actions:
            action.first_cycle()

    def update(self):
        for action in self.actions:
            if not action.done:
                if action.should_finish():
                    action.last_cycle()
                    action.done = True
                else:
                    action.update()

    def should_finish(self) -> bool:
        for action in self.actions:
            if not action.done:
                return False
        return True

    def last_cycle(self):
        pass

    def interrupt(self):
        for action in self.actions:
            action.interrupt()


class WaitFor(Action):
    def __init__(self, t: float, func: typing.Callable = None):
        self.t = t
        self.func: typing.Optional[typing.Callable] = func
        self.start = 0

    def first_cycle(self):
        self.start = time.time()

    def should_finish(self) -> bool:
        return (time.time() - self.start) > self.t

    def last_cycle(self):
        if self.func:
            self.func()


class Executor:
    action: typing.Optional[Action] = None

    def set_action(self, action: "Action", replace_if_busy=True):
        if self.action and not replace_if_busy: return
        self.action = action
        action.first_cycle()

    def update(self):
        if self.action:
            if self.action.should_finish():
                self.action.last_cycle()
                self.action = None
            else:
                self.action.update()
