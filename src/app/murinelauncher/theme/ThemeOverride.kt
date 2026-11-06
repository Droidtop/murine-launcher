package app.murinelauncher.theme

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import app.murinelauncher.icons.IconPackManager
import androidx.appcompat.app.AppCompatDelegate
import com.android.launcher3.LauncherAppState
import app.murinelauncher.hooks.MurineHooks

/**
 * Light or dark for the launcher: the host's one choice ([MurineHooks.theme]; droidtop: Settings > Appearance,
 * Droidtop/tracker#560), else the system's. Murine's own "UI theme" preference is gone: two settings for one job let
 * the home screen disagree with every other screen of its host.
 */
object ThemeOverride {
    const val THEME_SYSTEM = 0
    const val THEME_LIGHT = 1
    const val THEME_DARK = 2

    @JvmStatic
    fun getThemePref(context: Context): Int = MurineHooks.theme.choice(context)

    /**
     * Follows a change of the host's choice: the night mode is set again and, with an icon pack in use, icons are
     * reloaded so the pack's day or night set shows. Open launcher activities see themselves stale on their next start
     * and recreate ([isThemeStale]).
     */
    @JvmStatic
    fun watchAppearance(context: Context) {
        val app = context.applicationContext
        MurineHooks.theme.watch(app) {
            syncNightMode(app)
            if (IconPackManager.isAnyPackActive(app)) LauncherAppState.getInstance(app).model.forceReload()
        }
    }

    @JvmStatic
    fun syncNightMode(context: Context) {
        val mode = when (getThemePref(context)) {
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * @return true if night mode, checking the launcher's UI theme override if set, the system's otherwise.
     */
    @JvmStatic
    fun isNightMode(context: Context): Boolean = when (getThemePref(context)) {
        THEME_DARK -> true
        THEME_LIGHT -> false
        else -> (Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * @param prefContext where to read the theme pref from; lets [context] be another
     *        package's context (e.g. an icon pack) while honoring the launcher's setting.
     */
    @JvmStatic
    @JvmOverloads
    fun applyTheme(context: Context, prefContext: Context = context): Context {
        val themePref = getThemePref(prefContext)
        if (themePref == THEME_SYSTEM) return context

        val configuration = Configuration(context.resources.configuration)
        configuration.uiMode = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                if (themePref == THEME_DARK) Configuration.UI_MODE_NIGHT_YES
                else Configuration.UI_MODE_NIGHT_NO
        return context.createConfigurationContext(configuration)
    }

    @JvmStatic
    fun isThemeStale(context: Context): Boolean {
        val themePref = getThemePref(context)
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        val expectedNightMode = when (themePref) {
            THEME_SYSTEM -> Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            THEME_DARK -> Configuration.UI_MODE_NIGHT_YES
            else -> Configuration.UI_MODE_NIGHT_NO
        }
        return currentNightMode != expectedNightMode
    }
}
