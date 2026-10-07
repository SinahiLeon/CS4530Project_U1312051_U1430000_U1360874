package com.example.drawingdemov2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.drawingdemov2.ui.theme.DrawingDemoV2Theme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.OutlinedIconButton
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingDemoV2Theme {
                val drawingViewModel: DrawingViewModel = viewModel()
                DrawingCanvasPoints(drawingViewModel)
                //DrawCirlce()
                //DrawingCanvas(BrushType.RECTANGLE)
            }
        }
    }
}

@Composable
fun DrawingCanvasPoints(viewModel: DrawingViewModel) {
    var pExpanded by remember { mutableStateOf(false) }
    var cExpanded by remember { mutableStateOf(false) }
    var sExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                //We capture touch input with
                // pointerInput and detectDragGestures.
                .pointerInput(Unit) {
                    detectDragGestures(
                        // Canvas detects cursor down
                        onDragStart = { offset ->
                            viewModel.startStroke(offset)
                        },
                        // Canvas detects movement
                        onDrag = { change, _ ->
                            change.consume()
                            viewModel.addPoint(change.position)
                        },
                        // Canvas detects cursor up
                        onDragEnd = {
                            viewModel.endStroke()
                        }
                    )
                }
        ) {
            // Draw all completed strokes
            viewModel.strokes.forEach { stroke ->
                when (stroke.brushType) {

                    BrushType.LINE -> {
                        for (i in 0 until stroke.points.size - 1) {
                            drawLine(
                                color = stroke.color,
                                start = stroke.points[i],
                                end = stroke.points[i + 1],
                                strokeWidth = stroke.size
                            )
                        }
                    }

                    BrushType.CIRCLE -> {
                        stroke.points.forEach { point ->
                            drawCircle(
                                color = stroke.color,
                                radius = stroke.size,
                                center = point
                            )
                        }
                    }

                    BrushType.RECTANGLE -> {
                        stroke.points.forEach { point ->
                            drawRect(
                                color = stroke.color,
                                topLeft = Offset(
                                    point.x - stroke.size,
                                    point.y - stroke.size
                                ),
                                size = Size(
                                    stroke.size * 2,
                                    stroke.size * 2
                                )
                            )
                        }
                    }
                }
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors (
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box() {
                    IconButton(onClick = { pExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Open Pen menu"
                        )
                    }
                    DropdownMenu(
                        expanded = pExpanded,
                        onDismissRequest = {pExpanded = false}
                    ) {
                        Column() {
                            OutlinedIconButton(
                                onClick = { pExpanded = false; viewModel.changeBrushType(BrushType.LINE)},
                                border = if (viewModel.brushType == BrushType.LINE) {
                                    BorderStroke(width = 2.dp, color = Color.White)
                                } else {
                                    null;
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HorizontalRule,
                                    contentDescription = "Line Brush"
                                )
                            }
                            OutlinedIconButton(
                                onClick = { pExpanded = false; viewModel.changeBrushType(BrushType.CIRCLE)},
                                border = if (viewModel.brushType == BrushType.CIRCLE) {
                                    BorderStroke(width = 2.dp, color = Color.White)
                                } else {
                                    null;
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Circle,
                                    contentDescription = "Circle Brush"
                                )
                            }
                            OutlinedIconButton(
                                onClick = { pExpanded = false; viewModel.changeBrushType(BrushType.RECTANGLE)},
                                border = if (viewModel.brushType == BrushType.RECTANGLE) {
                                    BorderStroke(width = 2.dp, color = Color.White)
                                } else {
                                    null;
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Square,
                                    contentDescription = "Rectangle Brush"
                                )
                            }
                        }
                    }
                }



                OutlinedIconButton(
                    onClick = { viewModel.changeColor(Color.Black) },
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Black Pen",
                        tint = Color.Black
                    )
                }
                 IconButton(
                     onClick = { viewModel.changeColor(Color.Red) }
                        ) {
                     Icon(
                         imageVector = Icons.Default.Circle,
                         contentDescription = "Red Pen",
                         tint = Color.Red
                     )
                 }
                 IconButton(
                     onClick = { viewModel.changeColor(Color.Blue) }
                        ) {
                     Icon(
                         imageVector = Icons.Default.Circle,
                         contentDescription = "Blue Pen",
                         tint = Color.Blue
                     )
                 }
                 IconButton(
                     onClick = { viewModel.changeColor(Color.Green) }
                        ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Green Pen",
                        tint = Color.Green
                    )
                }


            }
        }
    }
}

enum class BrushType {
    LINE, CIRCLE, RECTANGLE
}

@Composable
fun DrawingCanvas(brushType: BrushType = BrushType.CIRCLE) {
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentStroke = listOf(offset)
                        strokes = strokes + listOf(currentStroke)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentStroke = currentStroke + change.position
                        strokes = strokes.dropLast(1) + listOf(currentStroke)
                    },
                    onDragEnd = { currentStroke = emptyList() }
                )
            }
    ) {
        strokes.forEach { stroke ->
            when (brushType) {
                BrushType.LINE -> {
                    for (i in 0 until stroke.size - 1) {
                        drawLine(Color.Black, stroke[i], stroke[i + 1], strokeWidth = 4f)
                    }
                }
                BrushType.CIRCLE -> {
                    stroke.forEach { point ->
                        drawCircle(Color.Red, radius = 15f, center = point)
                    }
                }
                BrushType.RECTANGLE -> {
                    stroke.forEach { point ->
                        drawRect(
                            Color.Black,
                            topLeft = Offset(point.x - 8f, point.y - 8f),
                            size = Size(30f, 30f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawCirlce() {
    Column (Modifier.fillMaxWidth().statusBarsPadding()) {
        Canvas(Modifier.size(100.dp)) {
            drawCircle(
                color = Color.Blue,
                radius = size.minDimension / 2
            )
        }
    }
}
