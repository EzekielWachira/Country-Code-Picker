package com.ezzy.ccp.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures what :ccp's baseline profile buys a consuming app.
 *
 * Every run uses the same compilation mode, so Compose's own profiles apply equally; the comparison
 * is between :app builds with and without the library's `baseline-prof.txt`. Each iteration starts
 * in a fresh process, so the picker's code runs for the first time — the cost the profile removes.
 *
 * Run with `./gradlew :baselineprofile:pixel6Api34BenchmarkReleaseAndroidTest
 * -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark`.
 */
@RunWith(AndroidJUnit4::class)
class PickerBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startup() = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = COMPILATION,
        startupMode = StartupMode.COLD,
        iterations = ITERATIONS,
    ) {
        startShowcase()
    }

    @Test
    fun browseCountrySheet() = measureJourney { browseCountrySheet() }

    @Test
    fun searchAndPickCountry() = measureJourney { searchAndPickCountry() }

    @Test
    fun typePhoneNumber() = measureJourney { typePhoneNumber() }

    private fun measureJourney(journey: MacrobenchmarkScope.() -> Unit) = rule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = COMPILATION,
        iterations = ITERATIONS,
        setupBlock = {
            killProcess()
            startShowcase()
        },
        measureBlock = journey,
    )

    private companion object {
        val COMPILATION = CompilationMode.Partial(BaselineProfileMode.Require)
        const val ITERATIONS = 10
    }
}
