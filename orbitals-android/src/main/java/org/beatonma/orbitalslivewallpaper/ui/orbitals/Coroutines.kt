package org.beatonma.orbitalslivewallpaper.ui.orbitals

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.beatonma.orbitalslivewallpaper.R


// https://gist.github.com/MiSikora/bbb9fc475569913f78e88a81932e06a9
val View.viewScope: CoroutineScope
    get() {
        val storedScope = getTag(R.id.view_coroutinescope_id) as? CoroutineScope
        if (storedScope != null) return storedScope

        val newScope = ViewCoroutineScope()
        if (isAttachedToWindow) {
            addOnAttachStateChangeListener(newScope)
            setTag(R.id.view_coroutinescope_id, newScope)
        } else newScope.cancel()

        return newScope
    }

private class ViewCoroutineScope : CoroutineScope, View.OnAttachStateChangeListener {
    override val coroutineContext = SupervisorJob() + Dispatchers.Main

    override fun onViewAttachedToWindow(view: View) = Unit

    override fun onViewDetachedFromWindow(view: View) {
        coroutineContext.cancel()
        view.setTag(R.id.view_coroutinescope_id, null)
    }
}
