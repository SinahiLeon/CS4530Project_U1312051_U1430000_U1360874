package com.example.klspaint


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.text.style.TextOverflow
import com.example.klspaint.ui.theme.Background
import com.example.klspaint.ui.theme.onBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(viewModel: KLSViewModel, myNavController: NavHostController) {
    var redraw by remember { mutableStateOf(0) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var drawingNameInput by remember { mutableStateOf("") }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = onBackground,
                ),
                title = {
                    Text(
                        text = viewModel.currentDrawingName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        myNavController.navigate("main")
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to home",
                            tint = onBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        drawingNameInput = viewModel.currentDrawingName
                        showSaveDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Save drawing",
                            tint = onBackground,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Background,
                contentColor = onBackground,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box { //Pen shape
                        IconButton(onClick = { viewModel.pExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Open pen menu",
                                Modifier.size(48.dp),
                                tint = onBackground
                            )
                        }
                        DropdownMenu(
                            expanded = viewModel.pExpanded,
                            onDismissRequest = { viewModel.pExpanded = false }
                        ) {
                            Column {
                                PenButton(viewModel, BrushType.LINE, "Line")
                                PenButton(viewModel, BrushType.CIRCLE, "Circle")
                                PenButton(viewModel, BrushType.RECTANGLE, "Rectangle")
                            }
                        }
                    }

                    Box { //Color
                        IconButton(
                            onClick = { viewModel.cExpanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = "Open color menu",
                                tint = viewModel.selectedColor,
                                modifier = if (viewModel.selectedColor == Color.Black) {
                                    Modifier
                                        .border(width = 2.dp, color = Color.White, CircleShape)
                                        .size(48.dp)
                                } else {
                                    Modifier.size(48.dp)
                                }
                            )
                        }
                        DropdownMenu(
                            expanded = viewModel.cExpanded,
                            onDismissRequest = { viewModel.cExpanded = false }
                        ) {
                            Column {
                                Row {
                                    ColorButton(viewModel, Color.Magenta, "Magenta")
                                    ColorButton(viewModel, Color.Green, "Green")
                                    ColorButton(viewModel, Color.White, "White")
                                }
                                Row {
                                    ColorButton(viewModel, Color.Red, "Red")
                                    ColorButton(viewModel, Color.Cyan, "Cyan")
                                    ColorButton(viewModel, Color.LightGray, "LightGray")
                                }
                                Row {
                                    ColorButton(viewModel, Color(0xFFFFA500), "Orange") //Orange
                                    ColorButton(viewModel, Color.Blue, "Blue")
                                    ColorButton(viewModel, Color.Gray, "Gray")
                                }
                                Row {
                                    ColorButton(viewModel, Color.Yellow, "Yellow")
                                    ColorButton(viewModel, Color(0xFFA500FF), "Purple") //Purple
                                    ColorButton(viewModel, Color.Black, "Black")
                                }
                            }
                        }
                    }
                    Box { //Pen size
                        IconButton(
                            onClick = { viewModel.sExpanded = true }
                        ) {
                            Icon(
                                imageVector = MaterialSymbolsArrowsOutput,
                                contentDescription = "Open size menu",
                                tint = onBackground,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = viewModel.sExpanded,
                            onDismissRequest = { viewModel.sExpanded = false },
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Circle,
                                    contentDescription = "Larger top",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(50.dp))
                                Slider(
                                    value = (viewModel.penSize - 8) / 40,
                                    onValueChange = { viewModel.changePenSize((it * 40) + 8) },
                                    modifier = Modifier
                                        .graphicsLayer({ rotationZ = 270f })
                                        .width(120.dp)
                                        .height(36.dp)
                                        .testTag("Slider")
                                )
                                Spacer(modifier = Modifier.height(50.dp))
                                Icon(
                                    imageVector = Icons.Default.Circle,
                                    contentDescription = "Larger bottom",
                                    tint = Color.White,
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                                redraw++
                            },
                            // Canvas detects cursor up
                            onDragEnd = {
                                viewModel.endStroke()
                            }
                        )
                    }
            ) {
                redraw
                // Draw the image
                viewModel.image?.let { it ->
                    drawImage(
                        image = it.asImageBitmap(),
                        dstSize = androidx.compose.ui.unit.IntSize(
                            size.width.toInt(),
                            size.height.toInt()
                        )
                    )
                }
            }
        }
    }

    if (showSaveDialog) {
        drawingNameInput = viewModel.currentDrawingName
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Enter Drawing Name") },
            containerColor = Background,
            text = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = drawingNameInput,
                        onValueChange = { drawingNameInput = it },
                        label = { Text("Drawing Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (drawingNameInput.isNotBlank()) {
                            showSaveDialog = false
                            viewModel.saveDrawing(drawingNameInput.trim())
                            myNavController.navigate("main")
                        }
                    },
                    enabled = drawingNameInput.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

enum class BrushType {
    LINE, CIRCLE, RECTANGLE
}

@Composable
fun PenButton(viewModel: KLSViewModel, brushType: BrushType, brushName: String) {
    OutlinedIconButton(
        onClick = { viewModel.pExpanded = false; viewModel.changeBrushType(brushType) },
        border = if (viewModel.brushType == brushType) {
            BorderStroke(width = 2.dp, color = Color.White)
        } else {
            null
        }
    ) {
        Icon(
            imageVector = if (brushType == BrushType.LINE) {
                Icons.Default.HorizontalRule
            } else if (brushType == BrushType.CIRCLE) {
                Icons.Default.Circle
            } else {
                Icons.Default.Square
            },
            contentDescription = "$brushName button"
        )
    }
}

@Composable
fun ColorButton(viewModel: KLSViewModel, color: Color, colorName: String) {
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
            contentDescription = "$colorName button",
            tint = color,
            modifier = if (color == Color.Black) {
                Modifier.border(width = 2.dp, color = Color.White, CircleShape)
            } else {
                Modifier
            }
        )
    }
}