package app.murinelauncher.hooks

import android.content.Context
import android.view.View
import android.widget.PopupMenu

/**
 * The launcher's small menus (a folder's options, a drawer tab's, an app's Drawer tabs) and the host's own entries on
 * the home screen's long-press menu and an app's long-press menu (droidtop: its Games, Quick Menu, companion and
 * plugin entries). Launcher3's own popups are not these and stay as they are.
 *
 * Entries are plain values ([Entry]): the host lists them and runs one by its [Entry.id]. Murine's default shows its
 * menus as a platform popup and lists no host entries.
 */
interface MenuHooks {
    /**
     * One menu row: its [title], [checked] for an option it turns on or off (null for an action), and [onSelect], run
     * after the menu closes; a null [onSelect] is a row that only states (a locked home screen).
     */
    class Row @JvmOverloads constructor(
        val title: String,
        val onSelect: Runnable?,
        val checked: Boolean? = null,
    )

    /** One host entry: [id] for [run], its [label], and [icon], a name Murine draws ("setting"; anything else: item). */
    class Entry @JvmOverloads constructor(val id: String, val label: String, val icon: String? = null)

    /**
     * Opens [rows] under [title] beside [anchor] (the view they belong to: this is the one Android type here), over a
     * [note] when there is one (why the rows cannot act).
     */
    fun show(anchor: View, title: String, rows: List<Row>, note: String?)

    /** The host's entries on the home screen's long-press menu, in order. */
    fun homeEntries(context: Context): List<Entry>

    /** The host's entry on the long-press menu of the app [packageName] labelled [label], or null for none. */
    fun appEntry(context: Context, packageName: String, label: String): Entry?

    /** Runs the host entry [id] from [context], the launcher's activity. */
    fun run(id: String, context: Context)

    companion object {
        /** Murine on its own: a platform popup, and no host entries. */
        @JvmField
        val DEFAULT: MenuHooks = object : MenuHooks {
            override fun show(anchor: View, title: String, rows: List<Row>, note: String?) {
                val popup = PopupMenu(anchor.context, anchor)
                note?.let { popup.menu.add(it).isEnabled = false }
                rows.forEachIndexed { i, row ->
                    val item = popup.menu.add(0, i, i + 1, row.title)
                    item.isEnabled = row.onSelect != null
                    row.checked?.let { item.isCheckable = true; item.isChecked = it }
                }
                popup.setOnMenuItemClickListener { item ->
                    rows.getOrNull(item.itemId)?.onSelect?.let { popup.dismiss(); it.run() }
                    true
                }
                popup.show()
            }

            override fun homeEntries(context: Context) = emptyList<Entry>()
            override fun appEntry(context: Context, packageName: String, label: String): Entry? = null
            override fun run(id: String, context: Context) = Unit
        }
    }
}
