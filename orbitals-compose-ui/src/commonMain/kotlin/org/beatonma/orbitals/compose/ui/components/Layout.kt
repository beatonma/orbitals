package org.beatonma.orbitals.compose.ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

/**
 * A LazyRow which can be scrolled with a mouse on desktop environments.
 */
@Composable
internal fun DraggableRow(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    content: LazyListScope.() -> Unit
) {
    val scope = rememberCoroutineScope()

    LazyRow(
        modifier
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        state.scrollBy(-delta)
                    }
                },
            ),
        state = state,
        contentPadding = contentPadding,
        content = content
    )
}

/**
 * A LazyColumn which can be scrolled with a mouse on desktop environments.
 */
@Composable
internal fun DraggableColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    content: LazyListScope.() -> Unit
) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        state.scrollBy(-delta)
                    }
                },
            ),
        state = state,
        contentPadding = contentPadding,
        content = content
    )
}
