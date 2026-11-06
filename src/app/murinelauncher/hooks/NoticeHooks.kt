package app.murinelauncher.hooks

/**
 * A short message that needs no action ("Copied", "Restarting in 3..."). Murine shows a toast ([MurineHooks.notices]);
 * a host may draw its own (droidtop: its flash notices).
 */
fun interface NoticeHooks {
    /** How the message reads: plain information, or a caution (something did not work, nothing needs doing). */
    enum class Tone { Info, Caution }

    fun flash(message: String, tone: Tone)
}
