/**
 * Copyright (c) 2025 Ezekiel Wachira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ezzy.ccp.countrypicker.model

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.model.Phone
import com.ezzy.ccp.model.SelectedCountry
import com.ezzy.ccp.model.Country as LegacyCountry

/**
 * Conversions between the canonical [Country] and the older public model types.
 *
 * These exist so the pre-existing `PhoneNumberInput` API keeps working unchanged while its
 * implementation runs on the new foundation. Every conversion goes through
 * [DefaultCountryDataSource], so there is no second source of country metadata anywhere.
 */

/**
 * Resolves a legacy [LegacyCountry] to its canonical counterpart.
 *
 * Matches on ISO alpha-2 only. The legacy model has no alpha-3 or region, so the dataset is the
 * authority for those; and matching on the dial code instead would be ambiguous for `+1` and `+44`.
 *
 * Returns `null` for a code the dataset does not know, which lets callers fall back rather than
 * fabricate a country.
 */
public fun LegacyCountry.toCanonical(): Country? = DefaultCountryDataSource.findByIso2(code)

/**
 * Projects a canonical [Country] into the legacy model.
 *
 * Lossy: ISO alpha-3, region and search aliases have nowhere to go, and the legacy model's
 * `ImageVector` flag is always `null` now that the bundled vectors are gone — the emoji flag
 * survives on [LegacyCountry.code] via `countryToFlagEmoji()`.
 */
public fun Country.toLegacy(): LegacyCountry = LegacyCountry(
    name = displayName,
    code = iso2Code,
    dialCode = dialCode,
)

/** Projects a canonical [Country] into the legacy [SelectedCountry] used by [Phone]. */
public fun Country.toSelectedCountry(): SelectedCountry = SelectedCountry(
    name = displayName,
    code = iso2Code,
    dialCode = dialCode,
    // The legacy model's `flag` is the emoji string, unlike LegacyCountry's ImageVector.
    flag = flag.orEmpty(),
)

/**
 * Projects a [PhoneNumberValue] into the legacy [Phone] snapshot.
 *
 * `phoneNumber` maps to the E.164 form, matching the old behaviour where that field carried
 * libphonenumber's `E164` output. It falls back to the raw digits when the number is not yet
 * parseable, because the old field was never null and existing callers do not expect it to become so.
 */
public fun PhoneNumberValue.toLegacyPhone(): Phone = Phone(
    formattedPhone = internationalNumber,
    phoneNumber = e164Number ?: nationalNumber,
    country = country.toSelectedCountry(),
    isValid = isValid,
)

/**
 * Resolves a country from an ISO code *or* a country name, the way the legacy `setCountry` parameter
 * worked.
 *
 * Tries ISO alpha-2, then alpha-3, then an exact display-name match, then an alias. Kept permissive
 * because the old parameter accepted `"KE"` and `"Kenya"` interchangeably and callers rely on both.
 */
public fun resolveCountryByCodeOrName(value: String?): Country? {
    val query = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    DefaultCountryDataSource.findByIso2(query)?.let { return it }

    val normalized = CountryTextNormalizer.normalize(query)
    return DefaultCountryDataSource.countries.firstOrNull { country ->
        country.iso3Code.equals(query, ignoreCase = true) ||
            country.normalizedName == normalized ||
            normalized in country.normalizedAliases
    }
}
