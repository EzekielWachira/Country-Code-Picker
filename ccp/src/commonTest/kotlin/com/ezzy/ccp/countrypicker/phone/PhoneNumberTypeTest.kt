package com.ezzy.ccp.countrypicker.phone

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.PhoneNumberType
import com.ezzy.ccp.countrypicker.model.PhoneNumberValidity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Number-type restriction behaviour.
 *
 * The case worth guarding hardest is [PhoneNumberType.FixedLineOrMobile]: a naive equality check
 * against [PhoneNumberType.Mobile] rejects every valid US and Canadian number, because that is the
 * only type libphonenumber can report for those regions.
 */
class PhoneNumberTypeTest {

    private val kenya = DefaultCountryDataSource.findByIso2("KE")!!
    private val us = DefaultCountryDataSource.findByIso2("US")!!
    private val uk = DefaultCountryDataSource.findByIso2("GB")!!

    @Test
    fun `unrestricted validation accepts any line type`() {
        // A UK landline: valid, and nothing says it has to be a mobile.
        val value = PhoneNumberValidator.evaluate("2079460000", uk)
        assertTrue(value.isValid)
        assertEquals(PhoneNumberValidity.Valid, value.validity)
    }

    @Test
    fun `mobile-only rejects a landline`() {
        val value = PhoneNumberValidator.evaluate("2079460000", uk, PhoneNumberType.SmsCapable)
        assertFalse(value.isValid)
        assertEquals(PhoneNumberValidity.WrongNumberType, value.validity)
        assertEquals(PhoneNumberType.FixedLine, value.numberType)
    }

    @Test
    fun `mobile-only accepts a mobile`() {
        val value = PhoneNumberValidator.evaluate("7400123456", uk, PhoneNumberType.SmsCapable)
        assertTrue(value.isValid)
        assertEquals(PhoneNumberType.Mobile, value.numberType)
    }

    @Test
    fun `mobile-only accepts a US number reported as fixed-line-or-mobile`() {
        val value = PhoneNumberValidator.evaluate("2015550123", us, PhoneNumberType.SmsCapable)
        assertEquals(PhoneNumberType.FixedLineOrMobile, value.numberType)
        assertTrue(
            value.isValid,
            message = "A US number is indistinguishable landline/mobile and must not be rejected",
        )
    }

    @Test
    fun `type restriction does not fire while the number is still incomplete`() {
        val value = PhoneNumberValidator.evaluate("71", kenya, PhoneNumberType.SmsCapable)
        assertFalse(value.isValid)
        // Incomplete, not WrongNumberType: the user is mid-entry, not holding a landline.
        assertEquals(PhoneNumberValidity.Incomplete, value.validity)
    }

    @Test
    fun `satisfies treats fixed-line-or-mobile as either`() {
        assertTrue(PhoneNumberType.FixedLineOrMobile.satisfies(PhoneNumberType.Mobile))
        assertTrue(PhoneNumberType.FixedLineOrMobile.satisfies(PhoneNumberType.FixedLine))
        assertTrue(PhoneNumberType.Mobile.satisfies(PhoneNumberType.FixedLineOrMobile))
        assertFalse(PhoneNumberType.Mobile.satisfies(PhoneNumberType.FixedLine))
        assertFalse(PhoneNumberType.TollFree.satisfies(PhoneNumberType.Mobile))
    }

    @Test
    fun `an empty restriction set accepts everything`() {
        assertTrue(PhoneNumberType.Unknown.satisfiesAny(emptySet()))
        assertFalse(PhoneNumberType.Unknown.satisfiesAny(PhoneNumberType.SmsCapable))
    }

    @Test
    fun `e164 is still produced for a valid number of the wrong type`() {
        // The number is real; it is only unusable for this particular flow. Callers that want to
        // store it anyway must not be handed a null.
        val value = PhoneNumberValidator.evaluate("2079460000", uk, PhoneNumberType.SmsCapable)
        assertEquals("+442079460000", value.e164Number)
    }
}
