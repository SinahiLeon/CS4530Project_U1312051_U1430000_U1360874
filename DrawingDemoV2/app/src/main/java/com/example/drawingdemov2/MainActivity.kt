package com.example.drawingdemov2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Slider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer

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
    var sliderValue by remember { mutableFloatStateOf(0.5f) }
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
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors (
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box() { //Pen shape
                    IconButton(onClick = { viewModel.pExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Open Pen menu",
                            Modifier.size(48.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = viewModel.pExpanded,
                        onDismissRequest = {viewModel.pExpanded = false}
                    ) {
                        Column() {
                            PenButton(viewModel, BrushType.LINE)
                            PenButton(viewModel, BrushType.CIRCLE)
                            PenButton(viewModel, BrushType.RECTANGLE)
                        }
                    }
                }

                Box() { //Color
                    IconButton(
                        onClick = { viewModel.cExpanded = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = "Black Pen",
                            tint = viewModel.selectedColor,
                            modifier = if(viewModel.selectedColor == Color.Black) {
                                Modifier.border(width = 2.dp, color = Color.White, CircleShape)
                                    .size(48.dp)
                            }
                            else {
                                Modifier.size(48.dp)
                            }
                        )
                    }
                    DropdownMenu(
                        expanded = viewModel.cExpanded,
                        onDismissRequest = {viewModel.cExpanded = false}
                    ) {
                        Column() {
                            Row() {
                                ColorButton(viewModel, Color.Magenta)
                                ColorButton(viewModel, Color.Green)
                                ColorButton(viewModel, Color.White)
                            }
                            Row() {
                                ColorButton(viewModel, Color.Red)
                                ColorButton(viewModel, Color.Cyan)
                                ColorButton(viewModel, Color.LightGray)
                            }
                            Row() {
                                ColorButton(viewModel, Color(0xFFFFA500)) //Orange
                                ColorButton(viewModel, Color.Blue)
                                ColorButton(viewModel, Color.Gray)
                            }
                            Row() {
                                ColorButton(viewModel, Color.Yellow)
                                ColorButton(viewModel, Color(0xFFA500FF)) //Purple
                                ColorButton(viewModel, Color.Black)
                            }
                        }
                    }
                }
                Box() { //Pen size
                    IconButton(
                        onClick = { viewModel.sExpanded = true }
                    ) {
                        Icon(
                            imageVector = MaterialSymbolsArrowsOutput,
                            contentDescription = "Placeholder",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = viewModel.sExpanded,
                        onDismissRequest = {viewModel.sExpanded = false},
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = "Larger",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(50.dp))
                            Slider(
                                value = (viewModel.penSize - 8) / 40,
                                onValueChange = {viewModel.changePenSize((it * 40) + 8)},
                                modifier = Modifier.graphicsLayer( {rotationZ = 270f})
                                    .width(120.dp)
                                    .height(36.dp)
                            )
                            Spacer(modifier = Modifier.height(50.dp))
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = "Larger",
                                tint = Color.White,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class BrushType {
    LINE, CIRCLE, RECTANGLE
}

@Composable
fun PenButton(viewModel: DrawingViewModel, brushType: BrushType) {
    OutlinedIconButton(
        onClick = { viewModel.pExpanded = false; viewModel.changeBrushType(brushType)},
        border = if (viewModel.brushType == brushType) {
            BorderStroke(width = 2.dp, color = Color.White)
        } else {
            null
        }
    ) {
        Icon(
            imageVector = if (brushType == BrushType.LINE) { Icons.Default.HorizontalRule }
            else if (brushType == BrushType.CIRCLE) { Icons.Default.Circle }
            else { Icons.Default.Square },
            contentDescription = "$brushType button"
        )
    }
}

@Composable
fun ColorButton(viewModel: DrawingViewModel, color: Color) {
    OutlinedIconButton(
        onClick = {
            viewModel.cExpanded = false
            viewModel.changeColor(color)
        },
        border = if (viewModel.selectedColor == color) {
            BorderStroke(width = 2.dp, color = Color.White)
        } else {
            null
        }
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = "$color button",
            tint = color,
            modifier = if(color == Color.Black) {
                Modifier.border(width = 2.dp, color = Color.White, CircleShape)
            }
            else {
                Modifier
            }
        )
    }
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
