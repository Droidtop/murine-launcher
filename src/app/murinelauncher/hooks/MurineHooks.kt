package app.murinelauncher.hooks

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * Where a host app changes what Murine does (Droidtop/tracker#921). Murine's own code calls these hooks instead of a
 * host's code; each area's default is Murine's own behaviour, so Murine runs as it is with nothing installed.
 *
 * The hook interfaces take plain Kotlin types, so a host that is not Android (Droidtop/tracker#919, the Linux UI) can
 * implement them; only the defaults here use Android. A host installs its implementations once, at application
 * start, after [attach] (droidtop: its `:shell-default-hooks` module, from its Application's onCreate).
 */
object MurineHooks {
    /** The application context Murine's own defaults post with; [com.android.launcher3.LauncherApplication] sets it. */
    @Volatile
    private var appContext: Context? = null

    @JvmStatic
    fun attach(context: Context) {
        appContext = context.applicationContext
    }

    /** Short messages that need no action. Murine's own: a toast. */
    @Volatile
    @JvmStatic
    var notices: NoticeHooks = NoticeHooks { message, _ -> appContext?.let { toast(it, message) } }

    /** The host's own home actions and accessibility service. Murine's own: none. */
    @Volatile
    @JvmStatic
    var actions: ActionHooks = ActionHooks.NONE

    private fun toast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
    }
}
