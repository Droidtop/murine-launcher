package app.murinelauncher.hooks

import android.view.KeyEvent
import android.view.MotionEvent
import com.android.launcher3.Launcher

/**
 * The host's work around the home screen: when it shows, on each key and touch, and on a long press of Back
 * (droidtop: its icon queued at the end of setup, its in-place hints, the pad's hint row, its mode menu). These sit
 * in Launcher3's own activity, so the launcher is handed over as it is. Murine's default does nothing, and a long
 * Back stays Android's.
 */
interface HomeHooks {
    /** The home has resumed on [launcher]. */
    fun onHomeShown(launcher: Launcher)

    /** A key event reached the home (before the launcher handles it). */
    fun onKey(event: KeyEvent)

    /** A touch event reached the home (before the launcher handles it). */
    fun onTouch(event: MotionEvent)

    /** Back was held on [launcher]; true when the host handled it. */
    fun onBackLongPress(launcher: Launcher): Boolean

    companion object {
        /** Murine on its own: nothing more. */
        @JvmField
        val DEFAULT: HomeHooks = object : HomeHooks {
            override fun onHomeShown(launcher: Launcher) = Unit
            override fun onKey(event: KeyEvent) = Unit
            override fun onTouch(event: MotionEvent) = Unit
            override fun onBackLongPress(launcher: Launcher) = false
        }
    }
}
