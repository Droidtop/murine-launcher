package app.murinelauncher.gestures

/**
 * What a home-screen gesture or a home icon's swipe does (Droidtop/tracker#564): one of the launcher's
 * [com.android.launcher3.touch.GestureAction]s, by its id, or starting an app. One type and one stored form for both,
 * so a gesture slot, an icon's swipe and an imported Nova or Apex action all mean the same thing. Pure.
 *
 * The stored form is the one droidtop has always written (an action's id, or [APP_PREFIX] and the component), so
 * saved slots and swipes keep their meaning; droidtop's own `dev.droidtop.runtime.HomeAction`, which its launcher
 * import uses, has the same form, and the two meet only through it (Droidtop/tracker#921).
 */
sealed class HomeAction {
    /** One of the launcher's [com.android.launcher3.touch.GestureAction]s, by id (`OPEN_DRAWER`, `LOCK_SCREEN`, ...). */
    data class Action(val id: String) : HomeAction()

    /** Start an app's launcher activity, [component] flattened (`pkg/pkg.Cls`). */
    data class Launch(val component: String) : HomeAction()

    /** The stored form: the action's id, or [APP_PREFIX] and the component. */
    val encoded: String
        get() = when (this) {
            is Action -> id
            is Launch -> APP_PREFIX + component
        }

    companion object {
        const val APP_PREFIX = "app:"
        const val NONE = "NONE"

        /** The stored form back; null for nothing stored, `NONE`, or an app with no component. */
        @JvmStatic
        fun decode(text: String?): HomeAction? {
            val value = text?.trim()?.takeIf { it.isNotEmpty() } ?: return null
            if (value.startsWith(APP_PREFIX)) {
                val component = value.removePrefix(APP_PREFIX).trim()
                return if ('/' in component) Launch(component) else null
            }
            return if (value == NONE) null else Action(value)
        }
    }
}
