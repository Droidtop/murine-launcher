package app.murinelauncher.home

import android.view.KeyEvent
import app.murinelauncher.hooks.MurineHooks
import com.android.launcher3.Launcher
import com.android.launcher3.LauncherState

/**
 * The launcher's pad map (Droidtop/tracker#699): the buttons a handheld's pad has beyond the D-pad and A, on the home
 * screen and in the drawer. L1/R1 turn the home pages, X opens the focused item's own menu (what a long press opens:
 * its shortcuts, Edit, Remove), Y opens the launcher's one search ([MurineHooks.search]), Start opens the host's mode
 * switcher (the SWITCH_MODE host action; with no host, Start stays the launcher's own key). B is Android's Back, which already
 * closes the drawer, a folder or a menu. The keys a focused view takes (A on an icon) never reach here.
 */
object PadKeys {
    enum class Action { PREVIOUS_PAGE, NEXT_PAGE, ITEM_MENU, SEARCH, MODE_SWITCHER }

    /** What [keyCode] means on the home. Pure, for the tests. */
    @JvmStatic
    fun actionFor(keyCode: Int): Action? = when (keyCode) {
        KeyEvent.KEYCODE_BUTTON_L1 -> Action.PREVIOUS_PAGE
        KeyEvent.KEYCODE_BUTTON_R1 -> Action.NEXT_PAGE
        KeyEvent.KEYCODE_BUTTON_X -> Action.ITEM_MENU
        KeyEvent.KEYCODE_BUTTON_Y -> Action.SEARCH
        KeyEvent.KEYCODE_BUTTON_START -> Action.MODE_SWITCHER
        else -> null
    }

    /** The page L1/R1 turn to from [current] of [count], held at the ends. Pure. */
    @JvmStatic
    fun pageFor(action: Action, current: Int, count: Int): Int = when (action) {
        Action.PREVIOUS_PAGE -> (current - 1).coerceAtLeast(0)
        Action.NEXT_PAGE -> (current + 1).coerceAtMost((count - 1).coerceAtLeast(0))
        else -> current
    }

    /** Handles a pad button on the launcher; null leaves the key to the launcher's own handling. */
    @JvmStatic
    fun onKeyDown(launcher: Launcher, keyCode: Int, event: KeyEvent): Boolean? {
        val action = actionFor(keyCode) ?: return null
        // One press, one step: a held button does not run through the pages or reopen the search.
        if (event.repeatCount > 0) return true
        when (action) {
            Action.PREVIOUS_PAGE, Action.NEXT_PAGE -> {
                if (!launcher.isInState(LauncherState.NORMAL)) return null
                val workspace = launcher.workspace
                val target = pageFor(action, workspace.nextPage, workspace.pageCount)
                if (target != workspace.nextPage) workspace.snapToPage(target)
            }
            Action.ITEM_MENU -> {
                val focused = launcher.currentFocus ?: return null
                focused.performLongClick()
            }
            Action.SEARCH -> MurineHooks.search.open(launcher, null)
            Action.MODE_SWITCHER -> if (!MurineHooks.actions.run("SWITCH_MODE", launcher)) return null
        }
        return true
    }
}
