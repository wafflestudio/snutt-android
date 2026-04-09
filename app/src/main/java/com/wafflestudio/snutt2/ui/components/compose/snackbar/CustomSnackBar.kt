package com.wafflestudio.snutt2.ui.components.compose.snackbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.State
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilterNotNull
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapTo
import com.wafflestudio.snutt2.ui.components.compose.clicks
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlin.math.max

/**
 * 안드로이드 Material 기본 제공 Snackbar 코드를 살짝 변형하여
 * 커스텀 스타일, 커스텀 지속시간 설정을 가능하게 함
 *
 * CustomSnackBar를 사용하기 위해서는
 * Scaffold, CustomSnackBarHostState, CustomSnackBar, CustomSnackBarDuration을 사용해야 함.
 *
 * CustomSnackBarDuration에서 inBetween이 fadeIn 및 fadeOut에 비해 충분히 크지 않은 경우,
 * 여러 SnackBar가 연속적으로 나타나는 과정에서 애니메이션이 부자연스러울 수 있음.
 *
 * @sample com.wafflestudio.snutt2.ui.components.compose.snackbar.SampleCustomSnackBarWithoutAction
 *
 * Action Label을 추가하고 싶다면, 아래와 같이 구현해야 함.
 * @sample com.wafflestudio.snutt2.ui.components.compose.snackbar.SampleCustomSnackBarWithAction
 */
@Composable
fun CustomSnackBar(
    modifier: Modifier = Modifier,
    snackBarData: CustomSnackBarStatus,
    passedData: CustomSnackBarData? = null,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = SnackbarDefaults.backgroundColor,
    contentStyle: TextStyle = LocalTextStyle.current,
    actionLabelStyle: TextStyle = LocalTextStyle.current,
    hazeState: HazeState = rememberHazeState(),
    elevation: Dp = 0.dp,
) {
    var savedSnackBarData by remember { mutableStateOf<CustomSnackBarData>(CustomSnackBarHostState.CustomSnackBarDataDefault()) }
    val modifiedSnackBarData = snackBarData.copy(passedData)
    when (modifiedSnackBarData) {
        is CustomSnackBarStatus.InVisible -> {}
        is CustomSnackBarStatus.FadeInOrBetween -> {
            savedSnackBarData = modifiedSnackBarData.data
            CustomSnackBar(
                modifier = modifier,
                snackBarData = modifiedSnackBarData.data,
                shape = shape,
                backgroundColor = backgroundColor,
                contentStyle = contentStyle,
                actionLabelStyle = actionLabelStyle,
                hazeState = hazeState,
                elevation = elevation,
            )
        }
        is CustomSnackBarStatus.FadeOut -> {
            CustomSnackBar(
                modifier = modifier,
                snackBarData = savedSnackBarData,
                shape = shape,
                backgroundColor = backgroundColor,
                contentStyle = contentStyle,
                actionLabelStyle = actionLabelStyle,
                hazeState = hazeState,
                elevation = elevation,
            )
        }
    }
}

@Composable
private fun CustomSnackBar(
    modifier: Modifier = Modifier,
    snackBarData: CustomSnackBarData,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = SnackbarDefaults.backgroundColor,
    contentStyle: TextStyle = LocalTextStyle.current,
    actionLabelStyle: TextStyle = LocalTextStyle.current,
    hazeState: HazeState = rememberHazeState(),
    elevation: Dp = 6.dp,
) {
    val actionLabel = snackBarData.actionLabel
    val actionComposable: (@Composable () -> Unit)? =
        if (actionLabel != null) {
            @Composable {
                Text(
                    modifier = Modifier.clicks {
                        snackBarData.performAction()
                    },
                    text = actionLabel,
                    style = actionLabelStyle,
                )
            }
        } else {
            null
        }
    CustomSnackBar(
        modifier = modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp),
        content = {
            Text(
                text = snackBarData.message,
                style = contentStyle,
            )
        },
        action = actionComposable,
        shape = shape,
        backgroundColor = backgroundColor,
        hazeState = hazeState,
        elevation = elevation,
    )
}

@Composable
private fun CustomSnackBar(
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = SnackbarDefaults.backgroundColor,
    hazeState: HazeState = rememberHazeState(),
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.clip(shape).hazeEffect(state = hazeState, style = HazeStyle(blurRadius = 4.dp, tint = null)),
        elevation = elevation,
        color = backgroundColor,
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            val textStyle = MaterialTheme.typography.body2
            ProvideTextStyle(value = textStyle) {
                when {
                    action == null -> CustomTextOnlySnackBar(content)
                    else -> CustomOneRowSnackBar(content, action)
                }
            }
        }
    }
}

@Composable
private fun CustomTextOnlySnackBar(content: @Composable () -> Unit) {
    Layout({
        Box(
            modifier =
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            content()
        }
    },) { measurables, constraints ->
        val textPlaceables = ArrayList<Placeable>(measurables.size)
        var firstBaseline = AlignmentLine.Unspecified
        var lastBaseline = AlignmentLine.Unspecified
        var height = 0

        measurables.fastForEach {
            val placeable = it.measure(constraints)
            textPlaceables.add(placeable)
            if (
                placeable[FirstBaseline] != AlignmentLine.Unspecified &&
                (
                    firstBaseline == AlignmentLine.Unspecified ||
                        placeable[FirstBaseline] < firstBaseline
                    )
            ) {
                firstBaseline = placeable[FirstBaseline]
            }
            if (
                placeable[LastBaseline] != AlignmentLine.Unspecified &&
                (
                    lastBaseline == AlignmentLine.Unspecified ||
                        placeable[LastBaseline] > lastBaseline
                    )
            ) {
                lastBaseline = placeable[LastBaseline]
            }
            height = max(height, placeable.height)
        }

        val hasText =
            firstBaseline != AlignmentLine.Unspecified && lastBaseline != AlignmentLine.Unspecified

        val minHeight =
            if (firstBaseline == lastBaseline || !hasText) {
                SnackBarMinHeightOneLine
            } else {
                SnackBarMinHeightTwoLines
            }
        val containerHeight = max(minHeight.roundToPx(), height)
        layout(constraints.maxWidth, containerHeight) {
            textPlaceables.fastForEach {
                val textPlaceY = (containerHeight - it.height) / 2
                it.placeRelative(0, textPlaceY)
            }
        }
    }
}

@Composable
private fun CustomOneRowSnackBar(text: @Composable () -> Unit, action: @Composable () -> Unit) {
    val textTag = "text"
    val actionTag = "action"
    Layout(
        {
            Box(Modifier.layoutId(textTag).padding(vertical = 12.dp)) { text() }
            Box(Modifier.layoutId(actionTag)) { action() }
        },
        modifier = Modifier.padding(horizontal = 16.dp),
    ) { measurables, constraints ->
        val buttonPlaceable =
            measurables.fastFirst { it.layoutId == actionTag }.measure(constraints)
        val textMaxWidth =
            (constraints.maxWidth - buttonPlaceable.width - TextEndExtraSpacing.roundToPx())
                .coerceAtLeast(constraints.minWidth)
        val textPlaceable =
            measurables
                .fastFirst { it.layoutId == textTag }
                .measure(constraints.copy(minHeight = 0, maxWidth = textMaxWidth))

        val firstTextBaseline = textPlaceable[FirstBaseline]
        val lastTextBaseline = textPlaceable[LastBaseline]
        val hasText =
            firstTextBaseline != AlignmentLine.Unspecified &&
                lastTextBaseline != AlignmentLine.Unspecified
        val isOneLine = firstTextBaseline == lastTextBaseline || !hasText
        val buttonPlaceX = constraints.maxWidth - buttonPlaceable.width

        val textPlaceY: Int
        val containerHeight: Int
        val buttonPlaceY: Int
        if (isOneLine) {
            val minContainerHeight = SnackBarMinHeightOneLine.roundToPx()
            val contentHeight = buttonPlaceable.height
            containerHeight = max(minContainerHeight, contentHeight)
            textPlaceY = (containerHeight - textPlaceable.height) / 2
            val buttonBaseline = buttonPlaceable[FirstBaseline]
            buttonPlaceY =
                buttonBaseline.let {
                    if (it != AlignmentLine.Unspecified) {
                        textPlaceY + firstTextBaseline - it
                    } else {
                        0
                    }
                }
        } else {
            val baselineOffset = HeightToFirstLine.roundToPx()
            textPlaceY = baselineOffset - firstTextBaseline
            val minContainerHeight = SnackBarMinHeightTwoLines.roundToPx()
            val contentHeight = textPlaceY + textPlaceable.height
            containerHeight = max(minContainerHeight, contentHeight)
            buttonPlaceY = (containerHeight - buttonPlaceable.height) / 2
        }

        layout(constraints.maxWidth, containerHeight) {
            textPlaceable.placeRelative(0, textPlaceY)
            buttonPlaceable.placeRelative(buttonPlaceX, buttonPlaceY)
        }
    }
}

@Composable
fun CustomSnackBarHost(
    hostState: CustomSnackBarHostState,
    modifier: Modifier = Modifier,
    snackBar: @Composable (CustomSnackBarData) -> Unit = { it ->
        CustomSnackBar(snackBarData = it)
    },
) {
    val currentSnackBarStatus = hostState.currentSnackBarData
    LaunchedEffect(currentSnackBarStatus) {
        if (currentSnackBarStatus is CustomSnackBarStatus.FadeInOrBetween) {
            val duration = currentSnackBarStatus.data.duration
            delay(duration.inBetween + duration.fadeIn)
            currentSnackBarStatus.data.dismiss()
        }
    }
    CustomFadeInOrBetweenFadeOutWithScale(
        currentSnackBarStatus = currentSnackBarStatus,
        modifier = modifier,
        content = snackBar,
    )
}

@Composable
private fun CustomFadeInOrBetweenFadeOutWithScale(
    currentSnackBarStatus: CustomSnackBarStatus,
    modifier: Modifier = Modifier,
    content: @Composable (CustomSnackBarData) -> Unit,
) {
    val current = when (currentSnackBarStatus) {
        is CustomSnackBarStatus.InVisible -> null
        is CustomSnackBarStatus.FadeInOrBetween -> currentSnackBarStatus.data
        is CustomSnackBarStatus.FadeOut -> null
    }
    val state = remember { CustomFadeInOrBetweenFadeOutState<CustomSnackBarData?>() }
    if (current != state.current) {
        state.current = current
        val keys = state.items.fastMap { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()
        keys.fastFilterNotNull().fastMapTo(state.items) { key ->
            CustomFadeInOrBetweenFadeOutAnimationItem(key) { children ->
                val isVisible = key == current
                val duration = if (isVisible) key.duration.fadeIn else key.duration.fadeOut
                val delay = key.duration.fadeOut
                val animationDelay =
                    if (isVisible && keys.fastFilterNotNull().size != 1) {
                        delay
                    } else {
                        0
                    }
                val opacity =
                    customAnimatedOpacity(
                        animation =
                        tween(
                            easing = LinearEasing,
                            delayMillis = animationDelay.toInt(),
                            durationMillis = duration.toInt(),
                        ),
                        visible = isVisible,
                        onAnimationFinish = {
                            if (key != state.current) {
                                state.items.removeAll { it.key == key }
                                state.scope?.invalidate()
                            }
                        },
                    )
                val scale =
                    customAnimatedScale(
                        animation =
                        tween(
                            easing = FastOutSlowInEasing,
                            delayMillis = animationDelay.toInt(),
                            durationMillis = duration.toInt(),
                        ),
                        visible = isVisible,
                    )
                Box(
                    Modifier.graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                        alpha = opacity.value,
                    )
                        .semantics {
                            if (isVisible) {
                                liveRegion = LiveRegionMode.Polite
                            }
                            dismiss {
                                key.dismiss()
                                true
                            }
                        },
                ) {
                    children()
                }
            }
        }
    }
    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.fastForEach { (item, opacity) ->
            key(item) {
                opacity { content(item!!) }
            }
        }
    }
}

private class CustomFadeInOrBetweenFadeOutState<T> {
    var current: Any? = Any()
    var items = mutableListOf<CustomFadeInOrBetweenFadeOutAnimationItem<T>>()
    var scope: RecomposeScope? = null
}

private data class CustomFadeInOrBetweenFadeOutAnimationItem<T>(
    val key: T,
    val transition: FadeInOrBetweenFadeOutTransition,
)

private typealias FadeInOrBetweenFadeOutTransition = @Composable (content: @Composable () -> Unit) -> Unit

@Composable
private fun customAnimatedOpacity(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {},
): State<Float> {
    val alpha = remember { Animatable(if (!visible) 1f else 0f) }
    LaunchedEffect(visible) {
        alpha.animateTo(if (visible) 1f else 0f, animationSpec = animation)
        onAnimationFinish()
    }
    return alpha.asState()
}

@Composable
private fun customAnimatedScale(animation: AnimationSpec<Float>, visible: Boolean): State<Float> {
//    val scale = remember { Animatable(if (!visible) 1f else 0.8f) }
//    LaunchedEffect(visible) {
//        scale.animateTo(if (visible) 1f else 0.8f, animationSpec = animation)
//    }
//    return scale.asState()

    // background blur 이동 현상 때문에 ScaleAnimation 삭제
    return remember { mutableStateOf(1F) }
}

private val TextEndExtraSpacing = 8.dp
private val SnackBarMinHeightOneLine = 48.dp
private val SnackBarMinHeightTwoLines = 68.dp
private val HeightToFirstLine = 30.dp
