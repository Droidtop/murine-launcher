package app.murinelauncher.widget.search

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import app.murinelauncher.hooks.MurineHooks
import app.murinelauncher.hooks.NoticeHooks
import com.android.launcher3.R

/**
 * A web search with the provider chosen in the search bar's settings ([SearchProvider], Settings > Search bar), else
 * the system's web search: Murine's own search ([app.murinelauncher.hooks.SearchHooks.DEFAULT]), and a host search's
 * web row (droidtop's "Search the web for ..."). It was a search box of its own, with its own history
 * (MurineSearchBoxView, deleted): two searches for one job.
 */
object WebSearch {
    private const val TAG = "WebSearch"

    @JvmStatic
    fun open(context: Context, query: String) {
        if (query.isBlank()) return
        try {
            context.startActivity(SearchProvider.current.buildSearchIntent(context, query).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Could not open search provider", e)
            system(context, query)
        } catch (e: SecurityException) {
            // A browser without an exported activity: the system's web search instead of a crash.
            Log.w(TAG, "Search provider activity not launchable", e)
            system(context, query)
        }
    }

    private fun system(context: Context, query: String) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_WEB_SEARCH).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(SearchManager.QUERY, query)
                },
            )
        } catch (e: RuntimeException) {
            // ActivityNotFoundException or SecurityException: no web search can be opened.
            Log.w(TAG, "No web search activity", e)
            MurineHooks.notices.flash(context.getString(R.string.activity_not_found), NoticeHooks.Tone.Caution)
        }
    }
}
