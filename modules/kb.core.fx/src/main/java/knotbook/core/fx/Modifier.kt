package knotbook.core.fx

import javafx.collections.ObservableList

@FXKtDSL
class Modifier<T>(private val list: ObservableList<T>) {
    operator fun T.unaryPlus() {
        list.add(this)
    }
}