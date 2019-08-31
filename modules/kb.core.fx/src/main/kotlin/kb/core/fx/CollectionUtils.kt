@file:Suppress("unused")

package kb.core.fx

import javafx.collections.FXCollections
import javafx.collections.ObservableList


@FXKtDSL
fun <T> List<T>.observable(): ObservableList<T> {
    return FXCollections.observableList(this)
}

@FXKtDSL
fun <T> MutableList<T>.addAll(vararg elements: T) {
    addAll(elements)
}