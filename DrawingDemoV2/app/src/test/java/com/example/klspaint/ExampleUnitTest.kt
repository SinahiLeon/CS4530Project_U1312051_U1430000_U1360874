package com.example.klspaint

import android.content.ContextWrapper
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DrawingUnitTest {

    private lateinit var viewModel: KLSViewModel

    @Before
    fun setup() {
        val fakeDao = object : KLSDAO {
            override fun allDrawings() = flowOf(emptyList<KLSDrawing>())
            override suspend fun addDrawing(data: KLSDrawing) {}
            override suspend fun deleteDrawing(data: KLSDrawing) {}
        }
        val scope = CoroutineScope(Dispatchers.Unconfined)
        val repository = KLSRepository(scope, fakeDao)
        val context = object : ContextWrapper(null) {}
        viewModel = KLSViewModel(repository, context)
    }

    @Test
    fun testInitializedState() {
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
        assertEquals(20f, viewModel.penSize)
    }

    @Test
    fun testChangeBrushType() {
        assertEquals(BrushType.LINE, viewModel.brushType)
        viewModel.changeBrushType(BrushType.CIRCLE)
        assertEquals(BrushType.CIRCLE, viewModel.brushType)
    }
}
