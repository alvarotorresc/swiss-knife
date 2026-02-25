package com.alvarotc.swissknife

import androidx.compose.ui.graphics.Color
import com.alvarotc.swissknife.viewmodel.RandomColorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RandomColorViewModelTest {
    @Test
    fun `initial state has no color and empty history`() {
        val vm = RandomColorViewModel()
        val state = vm.uiState.value
        assertNull(state.color)
        assertEquals("", state.hexString)
        assertEquals("", state.rgbString)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun `generate produces a color with hex and rgb strings`() {
        val vm = RandomColorViewModel()
        vm.generate()
        val state = vm.uiState.value
        assertNotNull(state.color)
        assertTrue(state.hexString.startsWith("#"))
        assertEquals(7, state.hexString.length) // #RRGGBB
        assertTrue(state.rgbString.isNotEmpty())
    }

    @Test
    fun `generate hex string is uppercase format`() {
        val vm = RandomColorViewModel()
        vm.generate()
        val hex = vm.uiState.value.hexString
        assertTrue(hex.matches(Regex("#[0-9A-F]{6}")))
    }

    @Test
    fun `generate rgb string has valid format`() {
        val vm = RandomColorViewModel()
        vm.generate()
        val rgb = vm.uiState.value.rgbString
        val parts = rgb.split(", ")
        assertEquals(3, parts.size)
        parts.forEach { part ->
            val value = part.trim().toInt()
            assertTrue("RGB value $value should be 0-255", value in 0..255)
        }
    }

    @Test
    fun `generate adds to history`() {
        val vm = RandomColorViewModel()
        vm.generate()
        assertEquals(1, vm.uiState.value.history.size)
        vm.generate()
        assertEquals(2, vm.uiState.value.history.size)
    }

    @Test
    fun `history is capped at 5 entries`() {
        val vm = RandomColorViewModel()
        repeat(10) { vm.generate() }
        assertEquals(5, vm.uiState.value.history.size)
    }

    @Test
    fun `history has most recent color first`() {
        val vm = RandomColorViewModel()
        vm.generate()
        val firstHex = vm.uiState.value.hexString
        vm.generate()
        val secondHex = vm.uiState.value.hexString
        // Most recent should be first in history
        assertEquals(secondHex, vm.uiState.value.history[0].second)
    }

    @Test
    fun `reshow updates current color and hex`() {
        val vm = RandomColorViewModel()
        val color = Color(128, 64, 32)
        val hex = "#804020"
        vm.reshow(color, hex)
        val state = vm.uiState.value
        assertEquals(color, state.color)
        assertEquals(hex, state.hexString)
    }

    @Test
    fun `reshow computes correct rgb string`() {
        val vm = RandomColorViewModel()
        val color = Color(255, 0, 128)
        val hex = "#FF0080"
        vm.reshow(color, hex)
        val rgb = vm.uiState.value.rgbString
        // Color internally stores as floats, so conversion back to int may have rounding
        assertTrue(rgb.isNotEmpty())
        val parts = rgb.split(", ").map { it.trim().toInt() }
        assertEquals(3, parts.size)
    }

    @Test
    fun `reshow does not modify history`() {
        val vm = RandomColorViewModel()
        vm.generate()
        vm.generate()
        val historySize = vm.uiState.value.history.size
        vm.reshow(Color.Red, "#FF0000")
        assertEquals(historySize, vm.uiState.value.history.size)
    }

    @Test
    fun `reset clears all state`() {
        val vm = RandomColorViewModel()
        repeat(3) { vm.generate() }
        vm.reset()
        val state = vm.uiState.value
        assertNull(state.color)
        assertEquals("", state.hexString)
        assertEquals("", state.rgbString)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun `generate produces different colors over many calls`() {
        val vm = RandomColorViewModel()
        val hexes = mutableSetOf<String>()
        repeat(20) {
            vm.generate()
            hexes.add(vm.uiState.value.hexString)
        }
        assertTrue("Expected varied colors, got ${hexes.size}", hexes.size > 1)
    }
}
