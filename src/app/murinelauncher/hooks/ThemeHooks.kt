package app.murinelauncher.hooks

import android.content.Context
import app.murinelauncher.theme.ThemeOverride

/**
 * Light or dark for the launcher, when the host has one choice for all its screens (droidtop: Settings > Appearance).
 * Murine's default follows the system.
 */
interface ThemeHooks {
    /** [ThemeOverride.THEME_SYSTEM], [ThemeOverride.THEME_LIGHT] or [ThemeOverride.THEME_DARK]. */
    fun choice(context: Context): Int

    /** Calls [onChange] whenever the choice changes, for as long as the process lives. */
    fun watch(context: Context, onChange: Runnable)

    companion object {
        /** Murine on its own: the system's light or dark. */
        @JvmField
        val DEFAULT: ThemeHooks = object : ThemeHooks {
            override fun choice(context: Context) = ThemeOverride.THEME_SYSTEM
            override fun watch(context: Context, onChange: Runnable) = Unit
        }
    }
}
