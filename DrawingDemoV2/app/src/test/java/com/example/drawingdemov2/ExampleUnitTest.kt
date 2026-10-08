package com.example.drawingdemov2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DrawingUnitTest {

    private lateinit var viewModel: DrawingViewModel

    @Before
    fun setup(){
        viewModel = DrawingViewModel()
    }

    @Test
    fun testInitializedState() {
        val strokes by mutableStateOf(listOf<Stroke>())

        assertEquals(strokes, viewModel.strokes)
        assertEquals(Color.Black, viewModel.selectedColor)
        assertEquals(8f, viewModel.penSize)
        assertEquals(BrushType.LINE, viewModel.brushType)
        assertFalse(viewModel.pExpanded)
        assertFalse(viewModel.cExpanded)
        assertFalse(viewModel.sExpanded)
    }

    @Test
    fun testChangeColor_colorIsPurple(){
        assertEquals(Color.Black, viewModel.selectedColor)

        viewModel.changeColor(Color(0xFFA500FF))

        assertEquals(Color(0xFFA500FF), viewModel.selectedColor)
    }

    @Test
    fun testChangePenSize_sizeIs20(){
        assertEquals(8f, viewModel.penSize)

        viewModel.changePenSize(20f)

        assertEquals(20f,viewModel.penSize)
    }

    @Test
    fun testStartStroke_addsSinglePoint(){
        viewModel.startStroke(Offset(10f, 10f))

        assertEquals(1, viewModel.strokes.size)

        val firstStroke = viewModel.strokes.first()
        assertEquals(listOf<Offset>(Offset(10f, 10f)), firstStroke.points)
    }

    @Test
    fun testAddPoint_addsMultiplePoints(){
        val point1 = Offset(10f, 10f)
        val point2 = Offset(20f, 20f)
        val point3 = Offset(30f, 30f)
        viewModel.startStroke(point1)
        viewModel.addPoint(point2)
        viewModel.addPoint(point3)

        assertEquals(1, viewModel.strokes.size)

        val currentStroke = viewModel.strokes.first()
        assertEquals(3, viewModel.getPointCount())
        assertEquals(listOf<Offset>(point1, point2, point3), currentStroke.points)
    }

    @Test
    fun testEndStroke_clearsCurrentStroke(){
        val point1 = Offset(10f, 10f)
        val point2 = Offset(20f, 20f)
        viewModel.startStroke(point1)
        viewModel.addPoint(point2)

        assertEquals(2, viewModel.getPointCount())

        viewModel.endStroke()

        assertEquals(0, viewModel.getPointCount())
    }
}