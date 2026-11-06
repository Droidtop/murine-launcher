package app.murinelauncher.hooks

import android.content.Context

/**
 * The host's rows on the launcher's settings: its own screens and places on the settings list, the settings toolbar's
 * action, and the launcher's rows that only a host can answer (the home screen choice, the taskbar, the Nova and Apex
 * import). Murine's default adds nothing and hides those rows.
 */
interface SettingsHooks {
    /**
     * One row the host adds: [id] for [run], its [title] and [summary], [icon] a drawable resource (0 for none),
     * [group] null for a row above the launcher's own or a section title to list it under after them, and [fragment]
     * a settings screen class the launcher's settings activity shows (null: pressing it runs [id]).
     */
    class Row @JvmOverloads constructor(
        val id: String,
        val title: String,
        val summary: String? = null,
        val icon: Int = 0,
        val group: String? = null,
        val fragment: String? = null,
    )

    /** The host's rows on the settings list, in order. */
    fun rows(context: Context): List<Row>

    /** The settings toolbar's action on every settings screen, or null for none. */
    fun toolbarRow(context: Context): Row?

    /** Whether the host answers the launcher's own row [key] (one of the keys above), so the row shows. */
    fun offers(key: String): Boolean

    /** Runs the host row or the launcher row [id] from [context], the settings activity. */
    fun run(id: String, context: Context)

    companion object {
        /** Murine on its own: no host rows. */
        @JvmField
        val DEFAULT: SettingsHooks = object : SettingsHooks {
            override fun rows(context: Context) = emptyList<Row>()
            override fun toolbarRow(context: Context): Row? = null
            override fun offers(key: String) = false
            override fun run(id: String, context: Context) = Unit
        }
    }
}
