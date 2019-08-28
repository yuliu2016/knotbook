@file:Suppress("unused")

package knotbook.core.fx

import javafx.collections.FXCollections
import javafx.collections.ObservableList


@FXKtDSL
fun <T> List<T>.observable(): ObservableList<T> {
    return FXCollections.observableList(this)
}