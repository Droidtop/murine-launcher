package app.murinelauncher.hooks

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.util.Log
import app.murinelauncher.widget.search.WebSearch
import com.android.launcher3.R

// Outside the companion: an interface companion with a @JvmField may hold only public @JvmField vals.
private const val TAG = "SearchHooks"

/**
 * The launcher's one search: the drawer's search field (a tap, or a key typed in the drawer), the home search bar,
 * a spoken query, and the Search gesture all open it. droidtop's is its library search, with the web as one row.
 * Murine's default is the web: [WebSearch] for a query, the system's search for none.
 */
fun interface SearchHooks {
    /** Opens the search from [context], the launcher's activity, with [query] typed in, or nothing typed for null. */
    fun open(context: Context, query: String?)

    companion object {
        /** Murine on its own: a web search for a query, the system's search for none. */
        @JvmField
        val DEFAULT = SearchHooks { context, query ->
            if (!query.isNullOrBlank()) {
                WebSearch.open(context, query)
            } else {
                try {
                    context.startActivity(Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (e: RuntimeException) {
                    // ActivityNotFoundException or SecurityException: nothing on the device searches.
                    Log.w(TAG, "No global search activity", e)
                    MurineHooks.notices.flash(context.getString(R.string.activity_not_found), NoticeHooks.Tone.Caution)
                }
            }
        }
    }
}
