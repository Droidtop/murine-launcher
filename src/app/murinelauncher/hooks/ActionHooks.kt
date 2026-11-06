package app.murinelauncher.hooks

import android.content.Context

/**
 * The host's own home actions: the gesture actions only a host has (droidtop: its Quick Menu, the mode switcher,
 * the library's Games, its companion, Clear all apps, its one search), and the accessibility service a host may
 * perform global actions through. Murine's own actions (the drawer, notifications, recents, lock, opening an app)
 * never come here.
 *
 * Ids are [com.android.launcher3.touch.GestureAction] ids, the stored form, so a saved slot keeps its meaning.
 * Murine's default offers no host action: those rows are hidden and a saved one does nothing.
 */
interface ActionHooks {
    /** Whether the host has the action [id], so the pickers show it. */
    fun offers(id: String): Boolean

    /**
     * Runs the host action [id] from [context], the launcher's activity (opening a host screen needs one: this is the
     * one Android type here). Returns whether it ran, so the gesture is consumed.
     */
    fun run(id: String, context: Context): Boolean

    /**
     * Performs an accessibility global action (`AccessibilityService.GLOBAL_ACTION_*`) through the host's
     * accessibility service. Returns false when the host has none connected, and the launcher then explains and
     * opens the system's accessibility settings, as Murine does on its own.
     */
    fun performGlobalAction(action: Int): Boolean

    companion object {
        /** Murine on its own: no host actions, no host accessibility service. */
        val NONE: ActionHooks = object : ActionHooks {
            override fun offers(id: String) = false
            override fun run(id: String, context: Context) = false
            override fun performGlobalAction(action: Int) = false
        }
    }
}
