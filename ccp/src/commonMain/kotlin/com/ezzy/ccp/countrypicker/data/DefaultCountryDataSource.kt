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

package com.ezzy.ccp.countrypicker.data

import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource.countries
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource.localizedNames
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource.primaryForDialCode
import com.ezzy.ccp.countrypicker.model.Country
import androidx.compose.ui.text.intl.Locale
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.model.appendCodePointCompat
import kotlin.concurrent.Volatile

/**
 * The library's bundled country dataset: every ISO 3166-1 country and territory that has an
 * international dial code, with ISO alpha-2, alpha-3, region and search aliases.
 *
 * This is the **single source of truth** for country metadata. The legacy
 * [com.ezzy.ccp.data.countryList] is derived from it rather than maintained separately.
 *
 * The rows are held as one delimited string and parsed exactly once, on first access. That keeps
 * the constant pool small (236 rows × 5 fields as separate object literals is a measurable APK and
 * class-init cost) and guarantees the dataset is never rebuilt during composition.
 *
 * Names are English-by-default. [localizedNames] resolves each country's name through the
 * platform's own region names (ICU on Android, `NSLocale` on iOS) when it has a translation, so a
 * French device shows "Allemagne" for `DE` without the library shipping its own translation table.
 */
public object DefaultCountryDataSource : CountryDataSource {

    /**
     * The list the picker shows: country names in the device's language, sorted for that language.
     *
     * This is **not** [countries]. [countries] is the English canonical dataset and is the identity
     * and lookup surface — it must stay stable, because a map keyed by a name that changes with the
     * device language is a map that stops working when the user switches language. What the *user
     * reads*, though, should be in their language, so that is what the data source serves.
     *
     * Resolution goes through [localizedNames], which is cached per locale: this is called on every
     * repository load, and rebuilding 236 `Country` objects plus a collator sort each time would be
     * a real cost on a cold sheet open.
     */
    override suspend fun load(): List<Country> = localizedNames(platformDefaultLocale())

    /**
     * English-named countries, sorted by name. Stable across calls **and across locales**.
     *
     * This is the canonical dataset: [byIso2], [findByIso2], [findByDialCode] and the legacy
     * [com.ezzy.ccp.data.countryList] are all derived from it, and all of them would break subtly if
     * the names moved under them when the device language changed. For names to show a user, use
     * [localizedNames] (or just let the repository do it — see [load]).
     */
    public val countries: List<Country> by lazy(LazyThreadSafetyMode.PUBLICATION) { parse() }

    /** Fast lookup by ISO alpha-2 code (uppercase). */
    public val byIso2: Map<String, Country> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        countries.associateBy { it.iso2Code }
    }

    /**
     * Looks up a country by ISO alpha-2 code, case-insensitively.
     *
     * The result carries the **English** [Country.displayName]. That is the right thing for lookup,
     * comparison and storage; it is the wrong thing to put straight on screen in a localized app —
     * use the [locale] overload for that.
     */
    public fun findByIso2(code: String?): Country? = code?.takeIf { it.isNotBlank() }
        ?.let { byIso2[normalizeIsoCode(it)] }

    /**
     * Looks up a country by ISO alpha-2 code with its name in [locale].
     *
     * Use this when seeding a selector with an initial country the user will *see*, so the name
     * matches what the picker itself shows:
     *
     * ```kotlin
     * var country by remember {
     *     mutableStateOf(DefaultCountryDataSource.findByIso2("DE", Locale.current))
     * }
     * ```
     *
     * Without it a French user sees "Germany" in the field until they open the sheet, where the
     * same country is listed as "Allemagne".
     *
     * Identity is unaffected either way: [Country] compares on [Country.iso2Code] alone, so a
     * localized instance and an English one are equal and interchangeable as map keys.
     */
    public fun findByIso2(code: String?, locale: Locale): Country? =
        code?.takeIf { it.isNotBlank() }
            ?.let { localizedByIso2(locale)[normalizeIsoCode(it)] }

    /**
     * The country a phone field starts on when the caller names none, with its name in [locale].
     *
     * Deliberately the first entry of the **English** [countries] rather than of the localized list:
     * "alphabetically first in the user's language" would silently change which country a field
     * defaults to depending on the device language, which is a surprising thing for a default to do.
     * Only the name is localized, so the field's helper and error text ("Enter a valid … number")
     * read in the user's language from the very first frame instead of switching once they pick.
     */
    public fun defaultInitialCountry(locale: Locale): Country =
        findByIso2(countries.first().iso2Code, locale) ?: countries.first()

    /**
     * Every country whose dial code matches [dialCode], with or without a leading `+`.
     *
     * Returns a list, not a single country, because dial codes are not unique: `+1` resolves to
     * 20+ North American territories and `+44` to four. Callers that need one country must apply
     * their own tie-break (see [primaryForDialCode]).
     */
    public fun findByDialCode(dialCode: String): List<Country> {
        val digits = dialCode.trim()
            .removePrefix("+")
        if (digits.isEmpty()) return emptyList()
        return countries.filter { it.dialCodeDigits == digits }
    }

    /**
     * The conventional "main" country for a shared dial code — `+1` → United States,
     * `+44` → United Kingdom, `+7` → Russia — falling back to the first match by name.
     *
     * libphonenumber makes the same choice via its region-code metadata; this mirrors it for the
     * cases where we only have a dial code and no national number to disambiguate with.
     */
    public fun primaryForDialCode(dialCode: String): Country? {
        val digits = dialCode.trim()
            .removePrefix("+")
        val candidates = findByDialCode(digits)
        if (candidates.size <= 1) return candidates.firstOrNull()
        val preferred = PRIMARY_BY_DIAL_CODE[digits]
        return candidates.firstOrNull { it.iso2Code == preferred } ?: candidates.first()
    }

    /**
     * Returns [countries] with [Country.displayName] replaced by the platform's translation for
     * [locale] where one exists, re-sorted with a locale-aware collator.
     *
     * Use this from a custom [CountryDataSource] when the app should show localized country names:
     * ```kotlin
     * val source = CountryDataSource { DefaultCountryDataSource.localizedNames(Locale.current) }
     * ```
     * The English name is kept as an alias so searching "Germany" still finds "Allemagne".
     */
    public fun localizedNames(locale: Locale): List<Country> {
        val key = locale.toLanguageTag()
        localizedCache[key]?.let { return it }
        return buildLocalizedNames(locale).also { localizedCache = localizedCache + (key to it) }
    }

    /**
     * Localized countries keyed by ISO alpha-2, for [findByIso2].
     *
     * Derived from the same cached list rather than rebuilt, so asking for one country does not pay
     * for localizing all 236 twice.
     */
    private fun localizedByIso2(locale: Locale): Map<String, Country> {
        val key = locale.toLanguageTag()
        localizedIndexCache[key]?.let { return it }
        return localizedNames(locale).associateBy { it.iso2Code }
            .also { localizedIndexCache = localizedIndexCache + (key to it) }
    }

    /**
     * Localized lists, keyed by language tag.
     *
     * [load] runs on every repository load, and building this means allocating 236 `Country`
     * objects, calling into ICU for each name, and running a collator sort — not something to
     * repeat every time a sheet opens. A process realistically sees one or two locales, so the map
     * is bounded in practice and is never evicted.
     *
     * Copy-on-write rather than a lock: two threads that miss at the same moment both build the
     * list and one of the two writes wins. Both results are equal, so the only cost of the race is
     * a redundant build, and the read path — every call after the first — never blocks.
     */
    @Volatile
    private var localizedCache: Map<String, List<Country>> = emptyMap()

    @Volatile
    private var localizedIndexCache: Map<String, Map<String, Country>> = emptyMap()

    private fun buildLocalizedNames(locale: Locale): List<Country> {
        val collator = platformCollator(locale)

        val named = if (locale.language == "en") {
            // English names come from the dataset, not from ICU.
            //
            // The dataset is curated to current ISO usage — "Cabo Verde", "Türkiye", "Czechia" —
            // and the platform's English is frequently behind it ("Cape Verde", "Turkey"), by an
            // amount that varies with the device's ICU version. Running English through ICU would
            // therefore silently undo that curation, and do it differently on different OS
            // releases. For every other language ICU is the only source there is, and a slightly
            // dated translation beats an English name.
            countries
        } else {
            countries.map { country -> country.withLocalizedName(locale) }
        }

        // Collated even for English: a plain string sort compares UTF-16 code units, which files
        // "Åland Islands" after "Zimbabwe" because Å is above Z. A collator puts it under A, which
        // is where every user looks for it.
        return named.sortedWith { a, b -> collator.compare(a.displayName, b.displayName) }
    }

    /**
     * This country with its name in [locale], or unchanged when the platform has no translation.
     *
     * The English name is preserved as a search alias, because people type the English name of a
     * country regardless of what language their phone is in.
     */
    private fun Country.withLocalizedName(locale: Locale): Country {
        val localized = platformCountryName(iso2Code, locale)
            // Both platforms echo the region code back when they have no translation, which would
            // otherwise show the user "KE" as a country name.
            ?.takeIf { it.isNotBlank() && !it.equals(iso2Code, ignoreCase = true) }

        return if (localized == null || localized == displayName) {
            this
        } else {
            copy(
                displayName = localized,
                alternativeNames = alternativeNames + displayName,
            )
        }
    }

    /** Uppercases and maps well-known non-ISO codes (`UK` → `GB`) to their canonical form. */
    public fun normalizeIsoCode(code: String): String {
        val upper = code.trim()
            .uppercase()
        return ISO_ALIASES[upper] ?: upper
    }

    private fun parse(): List<Country> = ROWS.lineSequence()
        .filter { it.isNotBlank() }
        .map { line ->
            val parts = line.split('|')
            val iso2 = parts[1]
            Country(
                iso2Code = iso2,
                iso3Code = parts[2],
                displayName = parts[0],
                dialCode = "+" + parts[3],
                flag = flagEmoji(iso2),
                region = requireNotNull(CountryRegion.fromKey(parts[4])) {
                    "Unknown region '${parts[4]}' for $iso2"
                },
                alternativeNames = SEARCH_ALIASES[iso2].orEmpty(),
            )
        }
        .sortedBy { it.normalizedName }
        .toList()

    /**
     * Builds the regional-indicator emoji flag for an ISO alpha-2 code.
     *
     * Returns `null` for codes with no assigned sequence (`XK`/Kosovo is user-assigned, not in
     * Unicode), which the UI renders as an ISO-code badge instead of an empty box.
     */
    public fun flagEmoji(iso2Code: String): String? {
        if (iso2Code.length != 2 || iso2Code in NO_EMOJI_FLAG) return null
        val upper = iso2Code.uppercase()
        if (upper.any { it !in 'A'..'Z' }) return null
        return buildString {
            for (char in upper) appendCodePointCompat(0x1F1E6 + (char.code - 'A'.code))
        }
    }

    /** ISO codes with no Unicode regional-indicator sequence. */
    private val NO_EMOJI_FLAG = setOf("XK")

    private val ISO_ALIASES = mapOf(
        "UK" to "GB",
        "EL" to "GR"
    )

    /** Conventional owner of a shared dial code, keyed by the dial code without `+`. */
    private val PRIMARY_BY_DIAL_CODE = mapOf(
        "1" to "US",
        "7" to "RU",
        "44" to "GB",
        "39" to "IT",
        "47" to "NO",
        "61" to "AU",
        "212" to "MA",
        "262" to "RE",
        "290" to "SH",
        "358" to "FI",
        "590" to "GP",
        "599" to "CW",
    )

    /**
     * Extra search terms per country: abbreviations, former names, endonyms and common
     * misspellings. Matched by [CountrySearchEngine] but never displayed.
     */
    private val SEARCH_ALIASES: Map<String, List<String>> = mapOf(
        "US" to listOf(
            "usa",
            "united states of america",
            "america"
        ),
        "GB" to listOf(
            "uk",
            "great britain",
            "britain",
            "england",
            "scotland",
            "wales"
        ),
        "AE" to listOf(
            "uae",
            "emirates"
        ),
        "KR" to listOf(
            "south korea",
            "republic of korea"
        ),
        "KP" to listOf(
            "dprk",
            "north korea"
        ),
        "NL" to listOf("holland"),
        "CI" to listOf("ivory coast"),
        "MM" to listOf("burma"),
        "CZ" to listOf("czech republic"),
        "SZ" to listOf("swaziland"),
        "TR" to listOf("turkey"),
        "CD" to listOf(
            "drc",
            "zaire",
            "democratic republic of the congo"
        ),
        "CG" to listOf("republic of the congo"),
        "CV" to listOf("cape verde"),
        "TL" to listOf("east timor"),
        "VA" to listOf("holy see"),
        "RU" to listOf("russian federation"),
        "VN" to listOf("viet nam"),
        "MK" to listOf("macedonia"),
        "DE" to listOf("deutschland"),
        "ES" to listOf(
            "espana",
            "españa"
        ),
        "IT" to listOf("italia"),
        "JP" to listOf(
            "nippon",
            "nihon"
        ),
        "CN" to listOf(
            "prc",
            "peoples republic of china"
        ),
        "IN" to listOf("bharat"),
        "GR" to listOf("hellas"),
        "FI" to listOf("suomi"),
        "SE" to listOf("sverige"),
        "NO" to listOf("norge"),
        "DK" to listOf("danmark"),
        "PL" to listOf("polska"),
        "BR" to listOf("brasil"),
        "CH" to listOf(
            "suisse",
            "schweiz",
            "svizzera"
        ),
        "AT" to listOf(
            "osterreich",
            "österreich"
        ),
        "BE" to listOf(
            "belgique",
            "belgie"
        ),
        "PT" to listOf("portugal"),
        "MX" to listOf("mejico"),
        "EG" to listOf("misr"),
        "ET" to listOf("abyssinia"),
        "ZA" to listOf("rsa"),
        "SA" to listOf(
            "ksa",
            "kingdom of saudi arabia"
        ),
        "LA" to listOf("lao"),
        "SY" to listOf("syrian arab republic"),
        "IR" to listOf("persia"),
        "BO" to listOf("plurinational state of bolivia"),
        "VE" to listOf("bolivarian republic of venezuela"),
        "TZ" to listOf("united republic of tanzania"),
        "MD" to listOf("republic of moldova"),
        "BN" to listOf("brunei darussalam"),
        "BA" to listOf("bosnia"),
        "AX" to listOf("aland"),
        "HK" to listOf("hong kong sar"),
        "MO" to listOf(
            "macao",
            "macau sar"
        ),
        "TW" to listOf(
            "republic of china",
            "chinese taipei"
        ),
        "PS" to listOf(
            "palestinian territories",
            "west bank",
            "gaza"
        ),
    )

    /**
     * `name|iso2|iso3|dialCode|region`, one country per line.
     *
     * Dial codes are stored without `+` and without separators, so North American territories
     * carry their full NANP prefix (e.g. Jamaica is `1876`, not `1`). That makes the dial code
     * directly usable for display and prefix matching.
     */
    private val ROWS: String = """
Afghanistan|AF|AFG|93|asia
Albania|AL|ALB|355|europe
Algeria|DZ|DZA|213|africa
American Samoa|AS|ASM|1684|oceania
Andorra|AD|AND|376|europe
Angola|AO|AGO|244|africa
Anguilla|AI|AIA|1264|americas
Antigua and Barbuda|AG|ATG|1268|americas
Argentina|AR|ARG|54|americas
Armenia|AM|ARM|374|asia
Aruba|AW|ABW|297|americas
Australia|AU|AUS|61|oceania
Austria|AT|AUT|43|europe
Azerbaijan|AZ|AZE|994|asia
Bahamas|BS|BHS|1242|americas
Bahrain|BH|BHR|973|asia
Bangladesh|BD|BGD|880|asia
Barbados|BB|BRB|1246|americas
Belarus|BY|BLR|375|europe
Belgium|BE|BEL|32|europe
Belize|BZ|BLZ|501|americas
Benin|BJ|BEN|229|africa
Bermuda|BM|BMU|1441|americas
Bhutan|BT|BTN|975|asia
Bolivia|BO|BOL|591|americas
Bosnia and Herzegovina|BA|BIH|387|europe
Botswana|BW|BWA|267|africa
Brazil|BR|BRA|55|americas
British Virgin Islands|VG|VGB|1284|americas
Brunei|BN|BRN|673|asia
Bulgaria|BG|BGR|359|europe
Burkina Faso|BF|BFA|226|africa
Burundi|BI|BDI|257|africa
Cabo Verde|CV|CPV|238|africa
Cambodia|KH|KHM|855|asia
Cameroon|CM|CMR|237|africa
Canada|CA|CAN|1|americas
Cayman Islands|KY|CYM|1345|americas
Central African Republic|CF|CAF|236|africa
Chad|TD|TCD|235|africa
Chile|CL|CHL|56|americas
China|CN|CHN|86|asia
Colombia|CO|COL|57|americas
Comoros|KM|COM|269|africa
Congo - Brazzaville|CG|COG|242|africa
Congo - Kinshasa|CD|COD|243|africa
Cook Islands|CK|COK|682|oceania
Costa Rica|CR|CRI|506|americas
Côte d'Ivoire|CI|CIV|225|africa
Croatia|HR|HRV|385|europe
Cuba|CU|CUB|53|americas
Curaçao|CW|CUW|599|americas
Cyprus|CY|CYP|357|europe
Czechia|CZ|CZE|420|europe
Denmark|DK|DNK|45|europe
Djibouti|DJ|DJI|253|africa
Dominica|DM|DMA|1767|americas
Dominican Republic|DO|DOM|1809|americas
Ecuador|EC|ECU|593|americas
Egypt|EG|EGY|20|africa
El Salvador|SV|SLV|503|americas
Equatorial Guinea|GQ|GNQ|240|africa
Eritrea|ER|ERI|291|africa
Estonia|EE|EST|372|europe
Eswatini|SZ|SWZ|268|africa
Ethiopia|ET|ETH|251|africa
Falkland Islands|FK|FLK|500|americas
Faroe Islands|FO|FRO|298|europe
Fiji|FJ|FJI|679|oceania
Finland|FI|FIN|358|europe
France|FR|FRA|33|europe
French Guiana|GF|GUF|594|americas
French Polynesia|PF|PYF|689|oceania
Gabon|GA|GAB|241|africa
Gambia|GM|GMB|220|africa
Georgia|GE|GEO|995|asia
Germany|DE|DEU|49|europe
Ghana|GH|GHA|233|africa
Gibraltar|GI|GIB|350|europe
Greece|GR|GRC|30|europe
Greenland|GL|GRL|299|americas
Grenada|GD|GRD|1473|americas
Guadeloupe|GP|GLP|590|americas
Guam|GU|GUM|1671|oceania
Guatemala|GT|GTM|502|americas
Guernsey|GG|GGY|44|europe
Guinea|GN|GIN|224|africa
Guinea-Bissau|GW|GNB|245|africa
Guyana|GY|GUY|592|americas
Haiti|HT|HTI|509|americas
Honduras|HN|HND|504|americas
Hong Kong|HK|HKG|852|asia
Hungary|HU|HUN|36|europe
Iceland|IS|ISL|354|europe
India|IN|IND|91|asia
Indonesia|ID|IDN|62|asia
Iran|IR|IRN|98|asia
Iraq|IQ|IRQ|964|asia
Ireland|IE|IRL|353|europe
Isle of Man|IM|IMN|44|europe
Israel|IL|ISR|972|asia
Italy|IT|ITA|39|europe
Jamaica|JM|JAM|1876|americas
Japan|JP|JPN|81|asia
Jersey|JE|JEY|44|europe
Jordan|JO|JOR|962|asia
Kazakhstan|KZ|KAZ|7|asia
Kenya|KE|KEN|254|africa
Kiribati|KI|KIR|686|oceania
Kosovo|XK|XKX|383|europe
Kuwait|KW|KWT|965|asia
Kyrgyzstan|KG|KGZ|996|asia
Laos|LA|LAO|856|asia
Latvia|LV|LVA|371|europe
Lebanon|LB|LBN|961|asia
Lesotho|LS|LSO|266|africa
Liberia|LR|LBR|231|africa
Libya|LY|LBY|218|africa
Liechtenstein|LI|LIE|423|europe
Lithuania|LT|LTU|370|europe
Luxembourg|LU|LUX|352|europe
Macau|MO|MAC|853|asia
Madagascar|MG|MDG|261|africa
Malawi|MW|MWI|265|africa
Malaysia|MY|MYS|60|asia
Maldives|MV|MDV|960|asia
Mali|ML|MLI|223|africa
Malta|MT|MLT|356|europe
Marshall Islands|MH|MHL|692|oceania
Martinique|MQ|MTQ|596|americas
Mauritania|MR|MRT|222|africa
Mauritius|MU|MUS|230|africa
Mayotte|YT|MYT|262|africa
Mexico|MX|MEX|52|americas
Micronesia|FM|FSM|691|oceania
Moldova|MD|MDA|373|europe
Monaco|MC|MCO|377|europe
Mongolia|MN|MNG|976|asia
Montenegro|ME|MNE|382|europe
Montserrat|MS|MSR|1664|americas
Morocco|MA|MAR|212|africa
Mozambique|MZ|MOZ|258|africa
Myanmar|MM|MMR|95|asia
Namibia|NA|NAM|264|africa
Nauru|NR|NRU|674|oceania
Nepal|NP|NPL|977|asia
Netherlands|NL|NLD|31|europe
New Caledonia|NC|NCL|687|oceania
New Zealand|NZ|NZL|64|oceania
Nicaragua|NI|NIC|505|americas
Niger|NE|NER|227|africa
Nigeria|NG|NGA|234|africa
Niue|NU|NIU|683|oceania
Norfolk Island|NF|NFK|672|oceania
North Korea|KP|PRK|850|asia
North Macedonia|MK|MKD|389|europe
Norway|NO|NOR|47|europe
Oman|OM|OMN|968|asia
Pakistan|PK|PAK|92|asia
Palau|PW|PLW|680|oceania
Palestine|PS|PSE|970|asia
Panama|PA|PAN|507|americas
Papua New Guinea|PG|PNG|675|oceania
Paraguay|PY|PRY|595|americas
Peru|PE|PER|51|americas
Philippines|PH|PHL|63|asia
Poland|PL|POL|48|europe
Portugal|PT|PRT|351|europe
Puerto Rico|PR|PRI|1787|americas
Qatar|QA|QAT|974|asia
Réunion|RE|REU|262|africa
Romania|RO|ROU|40|europe
Russia|RU|RUS|7|europe
Rwanda|RW|RWA|250|africa
Saint Helena|SH|SHN|290|africa
Saint Kitts and Nevis|KN|KNA|1869|americas
Saint Lucia|LC|LCA|1758|americas
Saint Martin|MF|MAF|590|americas
Saint Pierre and Miquelon|PM|SPM|508|americas
Saint Vincent and the Grenadines|VC|VCT|1784|americas
Samoa|WS|WSM|685|oceania
San Marino|SM|SMR|378|europe
Sao Tome and Principe|ST|STP|239|africa
Saudi Arabia|SA|SAU|966|asia
Senegal|SN|SEN|221|africa
Serbia|RS|SRB|381|europe
Seychelles|SC|SYC|248|africa
Sierra Leone|SL|SLE|232|africa
Singapore|SG|SGP|65|asia
Sint Maarten|SX|SXM|1721|americas
Slovakia|SK|SVK|421|europe
Slovenia|SI|SVN|386|europe
Solomon Islands|SB|SLB|677|oceania
Somalia|SO|SOM|252|africa
South Africa|ZA|ZAF|27|africa
South Korea|KR|KOR|82|asia
South Sudan|SS|SSD|211|africa
Spain|ES|ESP|34|europe
Sri Lanka|LK|LKA|94|asia
Sudan|SD|SDN|249|africa
Suriname|SR|SUR|597|americas
Sweden|SE|SWE|46|europe
Switzerland|CH|CHE|41|europe
Syria|SY|SYR|963|asia
Taiwan|TW|TWN|886|asia
Tajikistan|TJ|TJK|992|asia
Tanzania|TZ|TZA|255|africa
Thailand|TH|THA|66|asia
Timor-Leste|TL|TLS|670|asia
Togo|TG|TGO|228|africa
Tokelau|TK|TKL|690|oceania
Tonga|TO|TON|676|oceania
Trinidad and Tobago|TT|TTO|1868|americas
Tunisia|TN|TUN|216|africa
Türkiye|TR|TUR|90|asia
Turkmenistan|TM|TKM|993|asia
Turks and Caicos Islands|TC|TCA|1649|americas
Tuvalu|TV|TUV|688|oceania
US Virgin Islands|VI|VIR|1340|americas
Uganda|UG|UGA|256|africa
Ukraine|UA|UKR|380|europe
United Arab Emirates|AE|ARE|971|asia
United Kingdom|GB|GBR|44|europe
United States|US|USA|1|americas
Uruguay|UY|URY|598|americas
Uzbekistan|UZ|UZB|998|asia
Vanuatu|VU|VUT|678|oceania
Vatican City|VA|VAT|379|europe
Venezuela|VE|VEN|58|americas
Vietnam|VN|VNM|84|asia
Wallis and Futuna|WF|WLF|681|oceania
Western Sahara|EH|ESH|212|africa
Yemen|YE|YEM|967|asia
Zambia|ZM|ZMB|260|africa
Zimbabwe|ZW|ZWE|263|africa
Åland Islands|AX|ALA|358|europe
""".trimIndent()
}
