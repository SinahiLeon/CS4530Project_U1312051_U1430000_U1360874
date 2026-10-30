package com.example.klspaint

import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import org.junit.Assert.assertNotEquals
import org.junit.Before
import androidx.lifecycle.ViewModelProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DrawingInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // Open the "Create new drawing" button/page so that we can access the toolbar
    @Before
    fun setup() {
        try {
            composeTestRule.waitUntil(5000) {
                composeTestRule
                    .onAllNodesWithContentDescription("Create new drawing")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeTestRule
                .onNodeWithContentDescription("Create new drawing")
                .performClick()
        } catch (_: Exception) {
            // Already on canvas
        }
    }

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
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[KLSViewModel::class.java]

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
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[KLSViewModel::class.java]

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
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[KLSViewModel::class.java]

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
        val viewModel = androidx.lifecycle.ViewModelProvider(activity)[KLSViewModel::class.java]

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

    @Test
    fun testCreateNewDrawing() {
        val viewModel = ViewModelProvider(
            composeTestRule.activity
        )[KLSViewModel::class.java]

        composeTestRule.runOnUiThread {
            viewModel.createNewDrawing()
        }
        composeTestRule.waitForIdle()

        assertNotNull(viewModel.image)
        assertEquals("", viewModel.currentDrawingName)
    }

    @Test
    fun testRenameDrawing() {
        val viewModel = ViewModelProvider(
            composeTestRule.activity
        )[KLSViewModel::class.java]

        composeTestRule.runOnUiThread {
            viewModel.createNewDrawing()
            viewModel.saveDrawing("OG Name")
            viewModel.saveDrawing("Updated Name")
        }

        composeTestRule.waitForIdle()
        assertEquals("Updated Name", viewModel.currentDrawingName)
    }

    @Test
    fun TestNavigateBackToMainWindow() {
        composeTestRule
            .onNodeWithContentDescription("Back to home")
            .performClick()

        composeTestRule
            .onNodeWithText("My Drawings")
            .assertIsDisplayed()
    }


    @Test
    fun testSaveDrawingAppearsOnMainWindow() {
        val name = "TestSave_${System.currentTimeMillis()}"

        composeTestRule
            .onNodeWithContentDescription("Save drawing")
            .performClick()

        composeTestRule
            .onNodeWithText("Drawing Name")
            .performTextInput(name)

        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        // Confirm we returned to the main window
        composeTestRule
            .onNodeWithText("My Drawings")
            .assertIsDisplayed()

        // Wait for Room to update the saved drawings list
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText(name, useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(name, useUnmergedTree = true)
            .assertExists()
    }


    @Test
    fun testReopenSavedDrawing() {
        val name = "TestReopen_${System.currentTimeMillis()}"

        composeTestRule
            .onNodeWithContentDescription("Save drawing")
            .performClick()

        composeTestRule
            .onNodeWithText("Drawing Name")
            .performTextInput(name)

        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText(name)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(name).performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithContentDescription("Save drawing")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }



}

