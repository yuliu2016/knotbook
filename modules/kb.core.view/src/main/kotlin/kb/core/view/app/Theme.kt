package kb.core.view.app

@Suppress("unused")
enum class Theme(val viewStyle: String, val optionStyle: String) {
    Light("/light.css", "/light-option.css"),
    Dark("/dark.css", "/dark-option.css");
}