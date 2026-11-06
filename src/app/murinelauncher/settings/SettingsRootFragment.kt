package app.murinelauncher.settings

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceGroup
import app.murinelauncher.hooks.MurineHooks
import app.murinelauncher.hooks.SettingsHooks
import app.murinelauncher.settings.common.AbstractSettingsFragment
import com.android.launcher3.BuildConfig
import com.android.launcher3.R
import com.android.launcher3.util.DisplayController

public final class SettingsRootFragment: AbstractSettingsFragment() {

    companion object {
        @VisibleForTesting
        const val DEVELOPER_OPTIONS_KEY: String = "pref_developer_options"
        private const val HOST_ROW_PREFIX = "pref_host_"
    }

    override fun getPreferenceScreenResId() = R.xml.murine_prefs_root

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addHostRows()
    }

    /**
     * The host's rows (MurineHooks.settings; droidtop: Global settings first, then its places and its other modes'
     * settings in sections after the launcher's own rows).
     */
    private fun addHostRows() {
        val context = preferenceManager.context
        val screen = preferenceScreen
        val sections = LinkedHashMap<String, PreferenceGroup>()
        MurineHooks.settings.rows(context).forEachIndexed { i, row ->
            val preference = Preference(context).apply {
                key = HOST_ROW_PREFIX + row.id
                title = row.title
                summary = row.summary
                if (row.icon != 0) icon = ContextCompat.getDrawable(context, row.icon)
                if (row.fragment != null) {
                    fragment = row.fragment
                } else {
                    setOnPreferenceClickListener { MurineHooks.settings.run(row.id, it.context); true }
                }
            }
            val group = row.group
            if (group == null) {
                // Above the launcher's own rows, in the host's order.
                preference.order = i - 1000
                screen.addPreference(preference)
            } else {
                sections.getOrPut(group) {
                    PreferenceCategory(context).apply {
                        key = HOST_ROW_PREFIX + "group_" + sections.size
                        title = group
                        screen.addPreference(this)
                    }
                }.addPreference(preference)
            }
        }
    }

    override fun initPreference(preference: Preference, info: DisplayController.Info): Boolean {
        when (preference.key) {
            DEVELOPER_OPTIONS_KEY -> {
                if (BuildConfig.IS_STUDIO_BUILD) {
                    preference.setOrder(0)
                }
                return mDeveloperOptionsEnabled
            }
            else -> return true
        }
    }
}
