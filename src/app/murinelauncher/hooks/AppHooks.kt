package app.murinelauncher.hooks

/**
 * Which of the launcher's own package's components it lists while it hides its own package (the drawer, the
 * widget picker). Murine hides itself entirely; a host that is the same app as the launcher (droidtop) shows its one
 * icon and its widgets.
 */
fun interface AppHooks {
    /** Whether the launcher's own component [className] shows; [isWidgetProvider] is whether it provides a widget. */
    fun showsOwn(className: String, isWidgetProvider: Boolean): Boolean

    companion object {
        /** Murine on its own: none of its own components show. */
        @JvmField
        val DEFAULT = AppHooks { _, _ -> false }
    }
}
