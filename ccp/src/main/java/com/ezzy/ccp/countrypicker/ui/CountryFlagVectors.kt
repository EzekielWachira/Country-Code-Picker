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

package com.ezzy.ccp.countrypicker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.icons.Afganistan
import com.ezzy.ccp.icons.Albania
import com.ezzy.ccp.icons.Algeria
import com.ezzy.ccp.icons.Andorra
import com.ezzy.ccp.icons.Angola
import com.ezzy.ccp.icons.AntiguaBarbuda
import com.ezzy.ccp.icons.Argentina
import com.ezzy.ccp.icons.Armenia
import com.ezzy.ccp.icons.Australia
import com.ezzy.ccp.icons.Austria
import com.ezzy.ccp.icons.Azerbaijan
import com.ezzy.ccp.icons.Bahamas
import com.ezzy.ccp.icons.Bahrain
import com.ezzy.ccp.icons.Bangladesh
import com.ezzy.ccp.icons.Barbados
import com.ezzy.ccp.icons.Belarus
import com.ezzy.ccp.icons.Belgium
import com.ezzy.ccp.icons.Belize
import com.ezzy.ccp.icons.Benin
import com.ezzy.ccp.icons.Bhutan
import com.ezzy.ccp.icons.Bolivia
import com.ezzy.ccp.icons.BosniaHerzegovina
import com.ezzy.ccp.icons.Botswana
import com.ezzy.ccp.icons.Brazil
import com.ezzy.ccp.icons.Brunei
import com.ezzy.ccp.icons.Bulgaria
import com.ezzy.ccp.icons.BurkinaFaso
import com.ezzy.ccp.icons.Burundi
import com.ezzy.ccp.icons.CaboVerde
import com.ezzy.ccp.icons.Cambodia
import com.ezzy.ccp.icons.Cameroon
import com.ezzy.ccp.icons.Canada
import com.ezzy.ccp.icons.CentralAfricanRepublic
import com.ezzy.ccp.icons.Chad
import com.ezzy.ccp.icons.Chile
import com.ezzy.ccp.icons.China
import com.ezzy.ccp.icons.Colombia
import com.ezzy.ccp.icons.Comoros
import com.ezzy.ccp.icons.Congo
import com.ezzy.ccp.icons.CostaRica
import com.ezzy.ccp.icons.Croatia
import com.ezzy.ccp.icons.Cuba
import com.ezzy.ccp.icons.Cyprus
import com.ezzy.ccp.icons.CzechRepublic
import com.ezzy.ccp.icons.Denmark
import com.ezzy.ccp.icons.Djibouti
import com.ezzy.ccp.icons.DominicanRepublic
import com.ezzy.ccp.icons.Ecuador
import com.ezzy.ccp.icons.Egypt
import com.ezzy.ccp.icons.ElSalvador
import com.ezzy.ccp.icons.EquatorialGuinea
import com.ezzy.ccp.icons.Eritrea
import com.ezzy.ccp.icons.Estonia
import com.ezzy.ccp.icons.Eswatini
import com.ezzy.ccp.icons.Ethiopia
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.Fiji
import com.ezzy.ccp.icons.Finland
import com.ezzy.ccp.icons.France
import com.ezzy.ccp.icons.Georgia
import com.ezzy.ccp.icons.Germany
import com.ezzy.ccp.icons.Ghana
import com.ezzy.ccp.icons.Greece
import com.ezzy.ccp.icons.Guinea
import com.ezzy.ccp.icons.Hungary
import com.ezzy.ccp.icons.Iceland
import com.ezzy.ccp.icons.India
import com.ezzy.ccp.icons.Indonesia
import com.ezzy.ccp.icons.Iran
import com.ezzy.ccp.icons.Iraq
import com.ezzy.ccp.icons.Ireland
import com.ezzy.ccp.icons.Israel
import com.ezzy.ccp.icons.Italy
import com.ezzy.ccp.icons.Jamaica
import com.ezzy.ccp.icons.Japan
import com.ezzy.ccp.icons.Jordan
import com.ezzy.ccp.icons.Kazakhstan
import com.ezzy.ccp.icons.Kenya
import com.ezzy.ccp.icons.Kiribati
import com.ezzy.ccp.icons.Kuwait
import com.ezzy.ccp.icons.Latvia
import com.ezzy.ccp.icons.Lesotho
import com.ezzy.ccp.icons.Liberia
import com.ezzy.ccp.icons.Liechtenstein
import com.ezzy.ccp.icons.Luxembourg
import com.ezzy.ccp.icons.Malta
import com.ezzy.ccp.icons.Marshall
import com.ezzy.ccp.icons.Mexico
import com.ezzy.ccp.icons.Micronesia
import com.ezzy.ccp.icons.Monaco
import com.ezzy.ccp.icons.Morocco
import com.ezzy.ccp.icons.Namibia
import com.ezzy.ccp.icons.NewZealand
import com.ezzy.ccp.icons.Niger
import com.ezzy.ccp.icons.Nigeria
import com.ezzy.ccp.icons.Oman
import com.ezzy.ccp.icons.Palau
import com.ezzy.ccp.icons.PapuaNewGuinea
import com.ezzy.ccp.icons.Poland
import com.ezzy.ccp.icons.Portugal
import com.ezzy.ccp.icons.Qatar
import com.ezzy.ccp.icons.Russia
import com.ezzy.ccp.icons.Rwanda
import com.ezzy.ccp.icons.Samoa
import com.ezzy.ccp.icons.SanMarino
import com.ezzy.ccp.icons.SaudiArabia
import com.ezzy.ccp.icons.Senegal
import com.ezzy.ccp.icons.Seychelles
import com.ezzy.ccp.icons.SierraLeone
import com.ezzy.ccp.icons.SolomonIslands
import com.ezzy.ccp.icons.SouthAfrica
import com.ezzy.ccp.icons.SouthKorea
import com.ezzy.ccp.icons.SouthSudan
import com.ezzy.ccp.icons.Spain
import com.ezzy.ccp.icons.Sudan
import com.ezzy.ccp.icons.Sweden
import com.ezzy.ccp.icons.Syria
import com.ezzy.ccp.icons.Tanzania
import com.ezzy.ccp.icons.Togo
import com.ezzy.ccp.icons.Tonga
import com.ezzy.ccp.icons.Tunisia
import com.ezzy.ccp.icons.Turkey
import com.ezzy.ccp.icons.Tuvalu
import com.ezzy.ccp.icons.Uganda
import com.ezzy.ccp.icons.UnitedArabEmirates
import com.ezzy.ccp.icons.UnitedKingdom
import com.ezzy.ccp.icons.UnitedStates
import com.ezzy.ccp.icons.Vanuatu
import com.ezzy.ccp.icons.VaticanCity
import com.ezzy.ccp.icons.Yemen
import com.ezzy.ccp.icons.Zambia
import com.ezzy.ccp.icons.Zimbabwe

/**
 * The project's hand-drawn flag vectors, keyed by ISO alpha-2 code.
 *
 * These are the `EzzyIcons` assets the library already shipped. They are kept — deleting a set of
 * ~130 hand-authored vectors would be a real regression for anyone using them — but they are no
 * longer the *default* renderer. [CountryFlag] draws the platform emoji glyph by default, which
 * covers every one of the 236 countries in the dataset rather than the 131 that have a vector here.
 *
 * To use the vectors instead, pass this as the flag renderer:
 * ```kotlin
 * CountrySelector(
 *     selectedCountry = country,
 *     onCountrySelected = { country = it },
 *     flagContent = { c -> VectorCountryFlag(c) },
 * )
 * ```
 *
 * Coverage is partial by design; [flagVectorFor] returns `null` for the rest so callers can fall back
 * to the emoji renderer rather than showing a blank.
 */
object CountryFlagVectors {

    /** Looks up the vector for [iso2Code], or `null` when this set has no asset for it. */
    fun flagVectorFor(iso2Code: String): ImageVector? = byIso2[iso2Code.uppercase()]

    /** True when a vector exists for [iso2Code]. */
    fun hasVector(iso2Code: String): Boolean = flagVectorFor(iso2Code) != null

    /** How many countries this vector set covers. */
    val coverage: Int get() = byIso2.size

    private val byIso2: Map<String, ImageVector> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        mapOf(
        "AD" to EzzyIcons.Andorra,
        "AE" to EzzyIcons.UnitedArabEmirates,
        "AF" to EzzyIcons.Afganistan,
        "AG" to EzzyIcons.AntiguaBarbuda,
        "AL" to EzzyIcons.Albania,
        "AM" to EzzyIcons.Armenia,
        "AO" to EzzyIcons.Angola,
        "AR" to EzzyIcons.Argentina,
        "AT" to EzzyIcons.Austria,
        "AU" to EzzyIcons.Australia,
        "AZ" to EzzyIcons.Azerbaijan,
        "BA" to EzzyIcons.BosniaHerzegovina,
        "BB" to EzzyIcons.Barbados,
        "BD" to EzzyIcons.Bangladesh,
        "BE" to EzzyIcons.Belgium,
        "BF" to EzzyIcons.BurkinaFaso,
        "BG" to EzzyIcons.Bulgaria,
        "BH" to EzzyIcons.Bahrain,
        "BI" to EzzyIcons.Burundi,
        "BJ" to EzzyIcons.Benin,
        "BN" to EzzyIcons.Brunei,
        "BO" to EzzyIcons.Bolivia,
        "BR" to EzzyIcons.Brazil,
        "BS" to EzzyIcons.Bahamas,
        "BT" to EzzyIcons.Bhutan,
        "BW" to EzzyIcons.Botswana,
        "BY" to EzzyIcons.Belarus,
        "BZ" to EzzyIcons.Belize,
        "CA" to EzzyIcons.Canada,
        "CF" to EzzyIcons.CentralAfricanRepublic,
        "CG" to EzzyIcons.Congo,
        "CL" to EzzyIcons.Chile,
        "CM" to EzzyIcons.Cameroon,
        "CN" to EzzyIcons.China,
        "CO" to EzzyIcons.Colombia,
        "CR" to EzzyIcons.CostaRica,
        "CU" to EzzyIcons.Cuba,
        "CV" to EzzyIcons.CaboVerde,
        "CY" to EzzyIcons.Cyprus,
        "CZ" to EzzyIcons.CzechRepublic,
        "DE" to EzzyIcons.Germany,
        "DJ" to EzzyIcons.Djibouti,
        "DK" to EzzyIcons.Denmark,
        "DO" to EzzyIcons.DominicanRepublic,
        "DZ" to EzzyIcons.Algeria,
        "EC" to EzzyIcons.Ecuador,
        "EE" to EzzyIcons.Estonia,
        "EG" to EzzyIcons.Egypt,
        "ER" to EzzyIcons.Eritrea,
        "ES" to EzzyIcons.Spain,
        "ET" to EzzyIcons.Ethiopia,
        "FI" to EzzyIcons.Finland,
        "FJ" to EzzyIcons.Fiji,
        "FM" to EzzyIcons.Micronesia,
        "FR" to EzzyIcons.France,
        "GB" to EzzyIcons.UnitedKingdom,
        "GE" to EzzyIcons.Georgia,
        "GH" to EzzyIcons.Ghana,
        "GN" to EzzyIcons.Guinea,
        "GQ" to EzzyIcons.EquatorialGuinea,
        "GR" to EzzyIcons.Greece,
        "HR" to EzzyIcons.Croatia,
        "HU" to EzzyIcons.Hungary,
        "ID" to EzzyIcons.Indonesia,
        "IE" to EzzyIcons.Ireland,
        "IL" to EzzyIcons.Israel,
        "IN" to EzzyIcons.India,
        "IQ" to EzzyIcons.Iraq,
        "IR" to EzzyIcons.Iran,
        "IS" to EzzyIcons.Iceland,
        "IT" to EzzyIcons.Italy,
        "JM" to EzzyIcons.Jamaica,
        "JO" to EzzyIcons.Jordan,
        "JP" to EzzyIcons.Japan,
        "KE" to EzzyIcons.Kenya,
        "KH" to EzzyIcons.Cambodia,
        "KI" to EzzyIcons.Kiribati,
        "KM" to EzzyIcons.Comoros,
        "KR" to EzzyIcons.SouthKorea,
        "KW" to EzzyIcons.Kuwait,
        "KZ" to EzzyIcons.Kazakhstan,
        "LI" to EzzyIcons.Liechtenstein,
        "LR" to EzzyIcons.Liberia,
        "LS" to EzzyIcons.Lesotho,
        "LU" to EzzyIcons.Luxembourg,
        "LV" to EzzyIcons.Latvia,
        "MA" to EzzyIcons.Morocco,
        "MC" to EzzyIcons.Monaco,
        "MH" to EzzyIcons.Marshall,
        "MT" to EzzyIcons.Malta,
        "MX" to EzzyIcons.Mexico,
        "NA" to EzzyIcons.Namibia,
        "NE" to EzzyIcons.Niger,
        "NG" to EzzyIcons.Nigeria,
        "NZ" to EzzyIcons.NewZealand,
        "OM" to EzzyIcons.Oman,
        "PL" to EzzyIcons.Poland,
        "PT" to EzzyIcons.Portugal,
        "PW" to EzzyIcons.Palau,
        "QA" to EzzyIcons.Qatar,
        "RU" to EzzyIcons.Russia,
        "RW" to EzzyIcons.Rwanda,
        "SA" to EzzyIcons.SaudiArabia,
        "SC" to EzzyIcons.Seychelles,
        "SD" to EzzyIcons.Sudan,
        "SE" to EzzyIcons.Sweden,
        "SL" to EzzyIcons.SierraLeone,
        "SM" to EzzyIcons.SanMarino,
        "SN" to EzzyIcons.Senegal,
        "SS" to EzzyIcons.SouthSudan,
        "SV" to EzzyIcons.ElSalvador,
        "SY" to EzzyIcons.Syria,
        "SZ" to EzzyIcons.Eswatini,
        "TD" to EzzyIcons.Chad,
        "TG" to EzzyIcons.Togo,
        "TN" to EzzyIcons.Tunisia,
        "TO" to EzzyIcons.Tonga,
        "TR" to EzzyIcons.Turkey,
        "TV" to EzzyIcons.Tuvalu,
        "TZ" to EzzyIcons.Tanzania,
        "UG" to EzzyIcons.Uganda,
        "US" to EzzyIcons.UnitedStates,
        "VA" to EzzyIcons.VaticanCity,
        "VU" to EzzyIcons.Vanuatu,
        "WS" to EzzyIcons.Samoa,
        "YE" to EzzyIcons.Yemen,
        "ZA" to EzzyIcons.SouthAfrica,
        "ZM" to EzzyIcons.Zambia,
        "ZW" to EzzyIcons.Zimbabwe,
        "PG" to EzzyIcons.PapuaNewGuinea,
        "SB" to EzzyIcons.SolomonIslands,
        )
    }
}

/**
 * Draws a country's flag using the bundled [CountryFlagVectors], falling back to the emoji glyph when
 * this set has no asset for that country.
 *
 * Pass as `flagContent` to opt the whole picker into vector flags:
 * ```kotlin
 * CountrySelector(..., flagContent = { VectorCountryFlag(it) })
 * ```
 */
@Composable
fun VectorCountryFlag(
    country: Country,
    modifier: Modifier = Modifier,
) {
    val vector = CountryFlagVectors.flagVectorFor(country.iso2Code)
    if (vector == null) {
        // No vector for this country: emoji rather than an empty box, so coverage gaps are invisible.
        country.flag?.let { Text(text = it, modifier = modifier) }
        return
    }
    Image(
        imageVector = vector,
        // Decorative: the country name is announced by the row or selector that owns this flag.
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
    )
}
