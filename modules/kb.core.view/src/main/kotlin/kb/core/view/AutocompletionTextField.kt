package kb.core.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import kb.core.fx.add
import kb.core.fx.align
import kb.core.fx.hbox

import java.util.LinkedList
import java.util.SortedSet
import java.util.TreeSet

class AutocompletionTextField : TextField() {
    //Local variables
    //entries to autocomplete
    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    val entries: SortedSet<String>

    //popup GUI
    private val entriesPopup: ContextMenu

    init {
        this.entries = TreeSet()
        this.entriesPopup = ContextMenu()
        entriesPopup.minWidth = 200.0

        setListener()
    }

    /**
     * "Suggestion" specific listners
     */
    private fun setListener() {
        //Add "suggestions" by changing text
        textProperty().addListener { _, _, newValue ->
            val enteredText = text
            //always hide suggestion if nothing has been entered (only "spacebars" are dissalowed in TextFieldWithLengthLimit)
            if (enteredText == null || enteredText.isEmpty()) {
                entriesPopup.hide()
            } else {
                //filter all possible suggestions depends on "Text", case insensitive
                val filteredEntries = entries.filter { enteredText.toLowerCase() in it.toLowerCase() }
                //some suggestions are found
                if (filteredEntries.isNotEmpty()) {
                    //build popup - list of "CustomMenuItem"
                    populatePopup(filteredEntries, enteredText)

                    if (!entriesPopup.isShowing) { //optional
                        entriesPopup.show(this@AutocompletionTextField, Side.BOTTOM, 0.0, 0.0) //position of popup
                    }
                } else if (newValue.isBlank()) {
                    entriesPopup.hide()
                }
            }
        }

        //Hide always by focus-in (optional) and out
        focusedProperty().addListener { _, _, _ -> entriesPopup.hide() }
    }


    /**
     * Populate the entry set with the given search results. Display is limited to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private fun populatePopup(searchResult: List<String>, searchRequest: String) {
        //List of "suggestions"
        val menuItems = LinkedList<CustomMenuItem>()
        //List size - 10 or founded suggestions count
        val maxEntries = 10
        val count = searchResult.size.coerceAtMost(maxEntries)
        //Build list as set of labels
        for (i in 0 until count) {
            val result = searchResult[i]
            //label with graphic (text flow) to highlight founded subtext in suggestions
            val entryLabel = buildTextFlow(result, searchRequest)
            entryLabel.prefHeight = 16.0
            val item = CustomMenuItem(entryLabel, true)
            menuItems.add(item)

            //if any suggestion is select set it into text and close popup
            item.setOnAction {
                text = result
                positionCaret(result.length)
                entriesPopup.hide()
            }
        }

        //"Refresh" context menu
        entriesPopup.items.clear()
        entriesPopup.items.addAll(menuItems)
    }

    /**
     * Build TextFlow with selected text. Return "case" dependent.
     *
     * @param text - string with text
     * @param filter - string to select in text
     * @return - TextFlow
     */
    private fun buildTextFlow(text: String, filter: String): TextFlow {
        val filterIndex = text.toLowerCase().indexOf(filter.toLowerCase())
        val textBefore = Text(text.substring(0, filterIndex))
        val textAfter = Text(text.substring(filterIndex + filter.length))
        val textFilter = Text(text.substring(filterIndex, filterIndex + filter.length)) //instead of "filter" to keep all "case sensitive"
        textFilter.fill = Color.ORANGE
        textFilter.font = Font.font("Helvetica", FontWeight.BOLD, 12.0)
        return TextFlow(textBefore, textFilter, textAfter)
    }
}