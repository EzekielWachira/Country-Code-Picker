package com.ezzy.ccp.baselineprofile

import android.util.Log
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until


internal const val TARGET_PACKAGE = "com.ezzy.ccp"

private const val TIMEOUT_MS = 5_000L

internal fun MacrobenchmarkScope.startShowcase() {
    pressHome()
    startActivityAndWait()
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.browseCountrySheet() {
    openResidenceSheet()
    repeat(2) { flingList(Direction.DOWN) }
    repeat(2) { flingList(Direction.UP) }
    findOrNull(By.desc("Grid view"))?.let { grid ->
        grid.click()
        device.waitForIdle()
        flingList(Direction.DOWN)
        waitFor(By.desc("List view")).click()
        device.waitForIdle()
    }
    device.pressBack()
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.searchAndPickCountry() {
    openResidenceSheet()
    val search = waitFor(*textField("Search countries"))
    for (query in listOf("ken", "jpaan", "+49")) {
        search.text = query
        device.waitForIdle()
    }
    search.text = "Kenya"
    device.waitForIdle()
    waitFor(By.descStartsWith("Kenya, ")).click()
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.typePhoneNumber() {
    val field = scrollTo(textField("Phone number").first())
    field.click()
    device.waitForIdle()
    val digits = "07400123456"
    for (end in 1..digits.length) {
        field.text = digits.take(end)
    }
    device.waitForIdle()
    // Dismiss the keyboard so the next journey starts from the same screen state.
    device.pressBack()
    device.waitForIdle()
}

private fun MacrobenchmarkScope.openResidenceSheet() {
    scrollTo(By.descStartsWith("Country of residence")).click()
    waitFor(By.desc("Search countries"))
    device.waitForIdle()
}

private fun textField(label: String): Array<BySelector> = arrayOf(
    By.clazz("android.widget.EditText").hasDescendant(By.desc(label)),
    By.clazz("android.widget.EditText").desc(label),
)

private fun MacrobenchmarkScope.scrollTo(selector: BySelector): UiObject2 {
    findOrNull(selector)?.let { return it }
    repeat(10) {
        // Re-found every time: right after a cold start Compose is still rebuilding the
        // accessibility tree, and a page node held across scrolls goes stale.
        try {
            tallestScrollable().scroll(Direction.DOWN, 0.6f)
        } catch (_: StaleObjectException) {
            device.waitForIdle()
        }
        findOrNull(selector)?.let { return it }
    }
    fail("Not found on the showcase screen: $selector")
}

private fun MacrobenchmarkScope.flingList(direction: Direction) {
    repeat(3) {
        try {
            tallestScrollable().apply { setGestureMargin(device.displayWidth / 5) }.fling(direction)
            return
        } catch (_: StaleObjectException) {
            device.waitForIdle()
        }
    }
    fail("List stayed stale through three fling attempts")
}

private fun MacrobenchmarkScope.tallestScrollable(): UiObject2 {
    waitFor(By.scrollable(true))
    return device.findObjects(By.scrollable(true)).maxBy { it.visibleBounds.height() }
}

private fun MacrobenchmarkScope.waitFor(vararg selectors: BySelector): UiObject2 {
    val deadline = System.currentTimeMillis() + TIMEOUT_MS
    while (System.currentTimeMillis() < deadline) {
        selectors.firstNotNullOfOrNull { findOrNull(it) }?.let { return it }
        device.wait(Until.hasObject(selectors.first()), 250)
    }
    fail("Timed out waiting for: ${selectors.joinToString()}")
}

private fun MacrobenchmarkScope.fail(message: String): Nothing {
    val hierarchy = java.io.ByteArrayOutputStream().also { device.dumpWindowHierarchy(it) }.toString()
    Log.e("PickerJourneys", "$message\n$hierarchy")
    error(message)
}

private fun MacrobenchmarkScope.findOrNull(selector: BySelector): UiObject2? = device.findObject(selector)
