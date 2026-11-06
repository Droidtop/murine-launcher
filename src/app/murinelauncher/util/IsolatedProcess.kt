package app.murinelauncher.util

import android.os.Build
import android.os.Process

/**
 * Whether this is an isolated process: a service the app that holds the launcher runs with a random UID of its own and
 * no files, preferences or permissions (droidtop: a contained plugin's sandbox). The launcher's application starts
 * nothing there. Before API 28 there is no call for it, and the isolated UIDs are the fixed range 99000 to 99999 of
 * each user.
 */
object IsolatedProcess {
    @JvmStatic
    fun isIsolated(): Boolean =
        if (Build.VERSION.SDK_INT >= 28) Process.isIsolated() else (Process.myUid() % 100_000) in 99_000..99_999
}
