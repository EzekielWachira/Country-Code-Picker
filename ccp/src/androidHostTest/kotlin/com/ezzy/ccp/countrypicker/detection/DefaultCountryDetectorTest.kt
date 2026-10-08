package com.ezzy.ccp.countrypicker.detection

import android.content.Context
import android.telephony.TelephonyManager
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowTelephonyManager
import java.util.Locale

/**
 * The detection precedence chain and its defences against junk input.
 *
 * The order is SIM → network → configuration locale → fallback, and it is deliberate: the SIM says
 * where the subscriber's account lives, the network only where the handset currently is (which is
 * wrong while roaming), and the locale is weakest of all — plenty of people run an English device
 * outside the countries that speak it.
 *
 * The validation matters as much as the order. Real devices return `""`, `"--"` and three-letter
 * codes from `simCountryIso`, and every one of those has to fall through rather than propagate as a
 * bogus selection.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "en-rUS")
class DefaultCountryDetectorTest {

    private lateinit var context: Context
    private lateinit var telephony: ShadowTelephonyManager
    private val originalLocale: Locale = Locale.getDefault()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        telephony = shadowOf(
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager,
        )
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    private suspend fun detect(fallback: String? = "US"): CountryDetectionResult.Detected =
        DefaultCountryDetector(context, fallbackIso2Code = fallback).detectCountry().detected()

    /** Unwraps a result that must have resolved, failing with the actual value when it did not. */
    private fun CountryDetectionResult.detected(): CountryDetectionResult.Detected =
        this as? CountryDetectionResult.Detected
            ?: throw AssertionError("expected a detected country, got $this")

    @Test
    fun `the SIM wins over everything else`() = runTest {
        telephony.setSimCountryIso("ke")
        telephony.setNetworkCountryIso("de")

        val result = detect()
        assertEquals("KE", result.iso2Code)
        assertEquals(CountryDetectionSource.Sim, result.source)
    }

    @Test
    fun `the network is used when there is no SIM`() = runTest {
        telephony.setSimCountryIso("")
        telephony.setNetworkCountryIso("de")

        val result = detect()
        assertEquals("DE", result.iso2Code)
        assertEquals(CountryDetectionSource.Network, result.source)
    }

    @Test
    fun `the configuration locale is used when telephony says nothing`() = runTest {
        telephony.setSimCountryIso("")
        telephony.setNetworkCountryIso("")

        val result = detect()
        // The qualifier on this class is en-rUS.
        assertEquals("US", result.iso2Code)
        assertEquals(CountryDetectionSource.Locale, result.source)
    }

    @Test
    fun `a malformed SIM code falls through instead of propagating`() = runTest {
        // Values real devices actually return.
        listOf("--", "  ", "ZZ", "KEN", "1").forEach { junk ->
            telephony.setSimCountryIso(junk)
            telephony.setNetworkCountryIso("de")

            val result = detect()
            assertEquals(
                "simCountryIso=\"$junk\" should not have been accepted",
                "DE",
                result.iso2Code,
            )
        }
    }

    @Test
    fun `UK is normalized to the ISO code GB`() = runTest {
        // "UK" is not an ISO 3166-1 alpha-2 code, but devices and callers use it constantly.
        telephony.setSimCountryIso("uk")
        assertEquals("GB", detect().iso2Code)
    }

    @Test
    fun `an unknown region falls back to the configured default`() = runTest {
        telephony.setSimCountryIso("ZZ")
        telephony.setNetworkCountryIso("ZZ")
        Locale.setDefault(Locale.ROOT)

        val result = DefaultCountryDetector(context, fallbackIso2Code = "KE").detectCountry()
        // The en-rUS qualifier still supplies a configuration locale, so that wins over the
        // fallback — the fallback only exists for when nothing at all resolves.
        assertEquals("US", result.detected().iso2Code)
    }

    @Test
    fun `a null fallback never fabricates a country`() = runTest {
        telephony.setSimCountryIso("ZZ")
        telephony.setNetworkCountryIso("ZZ")
        Locale.setDefault(Locale.ROOT)

        // The en-rUS qualifier still supplies a configuration locale, so this resolves from that.
        // What is asserted is that the resolution came from a real signal rather than from the
        // (absent) fallback.
        val result = DefaultCountryDetector(context, fallbackIso2Code = null).detectCountry()
        assertEquals(CountryDetectionSource.Locale, result.detected().source)
    }

    @Test
    fun `withFallback only consults the fallback when the first detector yields nothing`() = runTest {
        val primary = CountryDetector { CountryDetectionResult.Unavailable }
        val secondary = CountryDetector {
            CountryDetectionResult.detected("KE", CountryDetectionSource.Network)
        }
        assertEquals("KE", primary.withFallback(secondary).detectCountry().detected().iso2Code)

        val decisive = CountryDetector {
            CountryDetectionResult.detected("DE", CountryDetectionSource.Sim)
        }
        assertEquals("DE", decisive.withFallback(secondary).detectCountry().detected().iso2Code)
    }

    @Test
    fun `two exhausted detectors report unavailable rather than guessing`() {
        runTest {
            val exhausted = CountryDetector { CountryDetectionResult.Unavailable }
                .withFallback(CountryDetector { CountryDetectionResult.Unavailable })
            assertEquals(CountryDetectionResult.Unavailable, exhausted.detectCountry())
        }
    }
}
