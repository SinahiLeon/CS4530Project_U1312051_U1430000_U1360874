package com.example.klspaint.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialSymbolsArrowsOutput: ImageVector
    get() {
        if (_MaterialSymbolsArrowsOutput != null) return _MaterialSymbolsArrowsOutput!!

        _MaterialSymbolsArrowsOutput = ImageVector.Builder(
            name = "arrows_output",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(120f, 840f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(104f)
                lineToRelative(124f, -124f)
                lineToRelative(56f, 56f)
                lineToRelative(-124f, 124f)
                horizontalLineToRelative(104f)
                verticalLineToRelative(80f)
                horizontalLineTo(120f)
                close()
                moveToRelative(480f, 0f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(104f)
                lineTo(580f, 636f)
                lineToRelative(56f, -56f)
                lineToRelative(124f, 124f)
                verticalLineToRelative(-104f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(240f)
                horizontalLineTo(600f)
                close()
                moveTo(324f, 380f)
                lineTo(200f, 256f)
                verticalLineToRelative(104f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(80f)
                horizontalLineTo(256f)
                lineToRelative(124f, 124f)
                lineToRelative(-56f, 56f)
                close()
                moveToRelative(312f, 0f)
                lineToRelative(-56f, -56f)
                lineToRelative(124f, -124f)
                horizontalLineTo(600f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-104f)
                lineTo(636f, 380f)
                close()
                moveTo(480f, 560f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(400f, 480f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(480f, 400f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(560f, 480f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(480f, 560f)
                close()
            }
        }.build()

        return _MaterialSymbolsArrowsOutput!!
    }

private var _MaterialSymbolsArrowsOutput: ImageVector? = null