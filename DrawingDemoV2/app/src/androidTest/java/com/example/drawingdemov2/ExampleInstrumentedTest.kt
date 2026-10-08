package com.example.drawingdemov2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import org.junit.Assert.assertNotEquals


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DrawingInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.drawingdemov2", appContext.packageName)
    }

    @Test
    fun testPenMenu_opensDropdownWithPens() {
        composeTestRule
            .onNodeWithContentDescription("Open pen menu")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Circle button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Line button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Rectangle button")
            .assertIsDisplayed()
    }

    @Test
    fun testPenMenu_changePen() {
        // get the viewModel from the emulator
        val activity = composeTestRule.activity
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[DrawingViewModel::class.java]

        composeTestRule
            .onNodeWithContentDescription("Open pen menu")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Circle button")
            .assertIsDisplayed()
            .performClick()

        assertEquals(BrushType.CIRCLE, viewModel.brushType)
    }

    @Test
    fun testColorMenu_opensColorPicker() {
        composeTestRule
            .onNodeWithContentDescription("Open color menu")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Magenta button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Green button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("White button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Red button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Cyan button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("LightGray button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Purple button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Blue button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Gray button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Yellow button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Orange button")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Black button")
            .assertIsDisplayed()
    }

    @Test
    fun testColorMenu_changeColor() {
        // get the viewModel from the emulator
        val activity = composeTestRule.activity
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[DrawingViewModel::class.java]

        composeTestRule
            .onNodeWithContentDescription("Open color menu")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Magenta button")
            .assertIsDisplayed()
            .performClick()

        assertEquals(Color.Magenta, viewModel.selectedColor)
    }

    @Test
    fun testSizeMenu_opensDropdownWithSizeSlider() {
        composeTestRule
            .onNodeWithContentDescription("Open size menu")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Larger top")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Larger bottom")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("Slider")
            .assertIsDisplayed()
    }

    @Test
    fun testSizeMenu_changeSize() {
        // get the viewModel from the emulator
        val activity = composeTestRule.activity
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[DrawingViewModel::class.java]

        composeTestRule
            .onNodeWithContentDescription("Open size menu")
            .performClick()

        composeTestRule
            .onNodeWithTag("Slider")
            .performTouchInput {
                click(center)
            }

        assertNotEquals(8f, viewModel.penSize)
    }

    @Test
    fun testAllMenus_changeAllPenSettings() {
        // get the viewModel from the emulator
        val activity = composeTestRule.activity
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[DrawingViewModel::class.java]

        composeTestRule
            .onNodeWithContentDescription("Open pen menu")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("Rectangle button")
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Open size menu")
            .performClick()
        composeTestRule
            .onNodeWithTag("Slider")
            .performTouchInput {
            click(center)
        }

        composeTestRule
            .onNodeWithContentDescription("Open color menu")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("Purple button")
            .assertIsDisplayed()
            .performClick()

        assertEquals(BrushType.RECTANGLE, viewModel.brushType)
        assertEquals(Color(0xFFA500FF), viewModel.selectedColor)
        assertNotEquals(8f, viewModel.penSize)
    }

}

// Test various stroke properties stored in view model
// Test that when canvas is clicked, point added