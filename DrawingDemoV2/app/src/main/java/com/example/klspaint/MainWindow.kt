package com.example.klspaint

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.klspaint.ui.theme.Background
import com.example.klspaint.ui.theme.onBackground

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWindow(
    klsViewModel: KLSViewModel,
    myNavController: NavHostController
) {

    // get saved drawings from Room
    val drawings by klsViewModel.allDrawings.collectAsStateWithLifecycle()

    // opens media gallery to select an image
    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            klsViewModel.importImage(it);
            myNavController.navigate("canvas")
        }
    }

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
                        "My Drawings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        klsViewModel.createNewDrawing()
                        myNavController.navigate("canvas")
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Create new drawing"
                        )
                    }
                    IconButton(onClick = {
                        launcher.launch("image/*")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Open gallery",
                            tint = onBackground,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = { presses++ }) {
//                Icon(Icons.Default.Add, contentDescription = "Add")
//            }
//        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(innerPadding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),

        ) {
                Spacer(modifier = Modifier.height(20.dp))

                //Display saved drawings
                for (drawing in drawings) {
                    if (drawing != null) {
                        Button ( onClick = {
                            klsViewModel.loadDrawing(drawing)
                            myNavController.navigate("canvas")
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Background,
                            contentColor = onBackground
                        ), modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Drawing ${drawing.id}"
                            }
                        ){
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text("${drawing.name}", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(onClick={
                                    klsViewModel.shareDrawing(drawing)
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                ), modifier = Modifier
                                    .semantics {
                                        contentDescription = "Share drawing ${drawing.id}"
                                    }
                                ){
                                    Text("Share")
                                }
                            }
                            }
                        }
                    }
                }
            }
        }



