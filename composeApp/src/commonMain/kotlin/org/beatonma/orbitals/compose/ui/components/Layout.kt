package org.beatonma.orbitals.compose.ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val DefaultColumnSpacing = 16.dp
private val DefaultRowSpacing = 16.dp

/**
 * A LazyRow which can be scrolled with a mouse on desktop environments.
 */
@Composable
internal fun DraggableRow(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    scope: CoroutineScope = rememberCoroutineScope(),
    content: LazyListScope.() -> Unit,
) {
    LazyRow(
        modifier.draggable(
            orientation = Orientation.Horizontal,
            state = rememberDraggableState { delta ->
                scope.launch {
                    state.scrollBy(-delta)
                }
            },
        ),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        state = state,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
internal fun SpacedRow(
    modifier: Modifier = Modifier,
    spacing: Dp = DefaultRowSpacing,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(
        spacing,
        horizontalAlignment
    ),
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier,
        horizontalArrangement,
        verticalAlignment,
        content,
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
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scope: CoroutineScope = rememberCoroutineScope(),
    content: LazyListScope.() -> Unit,
) {

    LazyColumn(
        modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                scope.launch {
                    state.scrollBy(-delta)
                }
            },
        ),
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
internal fun SpacedColumn(
    modifier: Modifier = Modifier,
    spacing: Dp = DefaultColumnSpacing,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(
        spacing,
        verticalAlignment
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier,
        verticalArrangement,
        horizontalAlignment,
        content,
    )
}
