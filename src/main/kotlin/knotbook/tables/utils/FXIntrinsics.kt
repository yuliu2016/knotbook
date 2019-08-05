@file:Suppress("unused")

package knotbook.tables.utils

import javafx.event.Event
import javafx.event.EventTarget

fun fireEvent(target: EventTarget, event: Event) {
    Event.fireEvent(target, event)
}