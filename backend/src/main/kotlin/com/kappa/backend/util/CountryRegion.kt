package com.kappa.backend.util

object CountryRegion {
    private val isoToRegion = mapOf(
        // Africa
        "DZ" to "Africa", "AO" to "Africa", "BJ" to "Africa", "BW" to "Africa", "BF" to "Africa",
        "BI" to "Africa", "CV" to "Africa", "CM" to "Africa", "CF" to "Africa", "TD" to "Africa",
        "KM" to "Africa", "CD" to "Africa", "CG" to "Africa", "CI" to "Africa", "DJ" to "Africa",
        "EG" to "Africa", "GQ" to "Africa", "ER" to "Africa", "SZ" to "Africa", "ET" to "Africa",
        "GA" to "Africa", "GM" to "Africa", "GH" to "Africa", "GN" to "Africa", "GW" to "Africa",
        "KE" to "Africa", "LS" to "Africa", "LR" to "Africa", "LY" to "Africa", "MG" to "Africa",
        "MW" to "Africa", "ML" to "Africa", "MR" to "Africa", "MU" to "Africa", "YT" to "Africa",
        "MA" to "Africa", "MZ" to "Africa", "NA" to "Africa", "NE" to "Africa", "NG" to "Africa",
        "RE" to "Africa", "RW" to "Africa", "SH" to "Africa", "ST" to "Africa", "SN" to "Africa",
        "SC" to "Africa", "SL" to "Africa", "SO" to "Africa", "ZA" to "Africa", "SS" to "Africa",
        "SD" to "Africa", "TZ" to "Africa", "TG" to "Africa", "TN" to "Africa", "UG" to "Africa",
        "EH" to "Africa", "ZM" to "Africa", "ZW" to "Africa",
        // Asia
        "AF" to "Asia", "AM" to "Asia", "AZ" to "Asia", "BH" to "Asia", "BD" to "Asia", "BT" to "Asia",
        "BN" to "Asia", "KH" to "Asia", "CN" to "Asia", "GE" to "Asia", "HK" to "Asia", "IN" to "Asia",
        "ID" to "Asia", "IR" to "Asia", "IQ" to "Asia", "IL" to "Asia", "JP" to "Asia", "JO" to "Asia",
        "KZ" to "Asia", "KW" to "Asia", "KG" to "Asia", "LA" to "Asia", "LB" to "Asia", "MO" to "Asia",
        "MY" to "Asia", "MV" to "Asia", "MN" to "Asia", "MM" to "Asia", "NP" to "Asia", "KP" to "Asia",
        "OM" to "Asia", "PK" to "Asia", "PS" to "Asia", "PH" to "Asia", "QA" to "Asia", "SA" to "Asia",
        "SG" to "Asia", "KR" to "Asia", "LK" to "Asia", "SY" to "Asia", "TW" to "Asia", "TJ" to "Asia",
        "TH" to "Asia", "TL" to "Asia", "TR" to "Asia", "TM" to "Asia", "AE" to "Asia", "UZ" to "Asia",
        "VN" to "Asia", "YE" to "Asia",
        // Europe
        "AL" to "Europe", "AD" to "Europe", "AT" to "Europe", "BY" to "Europe", "BE" to "Europe",
        "BA" to "Europe", "BG" to "Europe", "HR" to "Europe", "CY" to "Europe", "CZ" to "Europe",
        "DK" to "Europe", "EE" to "Europe", "FO" to "Europe", "FI" to "Europe", "FR" to "Europe",
        "DE" to "Europe", "GI" to "Europe", "GR" to "Europe", "HU" to "Europe", "IS" to "Europe",
        "IE" to "Europe", "IM" to "Europe", "IT" to "Europe", "XK" to "Europe", "LV" to "Europe",
        "LI" to "Europe", "LT" to "Europe", "LU" to "Europe", "MT" to "Europe", "MD" to "Europe",
        "MC" to "Europe", "ME" to "Europe", "NL" to "Europe", "MK" to "Europe", "NO" to "Europe",
        "PL" to "Europe", "PT" to "Europe", "RO" to "Europe", "RU" to "Europe", "SM" to "Europe",
        "RS" to "Europe", "SK" to "Europe", "SI" to "Europe", "ES" to "Europe", "SE" to "Europe",
        "CH" to "Europe", "UA" to "Europe", "GB" to "Europe", "VA" to "Europe",
        // North America
        "AG" to "North America", "AI" to "North America", "AW" to "North America", "BS" to "North America",
        "BB" to "North America", "BZ" to "North America", "BM" to "North America", "VG" to "North America",
        "CA" to "North America", "KY" to "North America", "CR" to "North America", "CU" to "North America",
        "CW" to "North America", "DM" to "North America", "DO" to "North America", "SV" to "North America",
        "GL" to "North America", "GD" to "North America", "GP" to "North America", "GT" to "North America",
        "HT" to "North America", "HN" to "North America", "JM" to "North America", "MQ" to "North America",
        "MX" to "North America", "MS" to "North America", "NI" to "North America", "PA" to "North America",
        "PR" to "North America", "BL" to "North America", "KN" to "North America", "LC" to "North America",
        "MF" to "North America", "PM" to "North America", "VC" to "North America", "SX" to "North America",
        "TT" to "North America", "TC" to "North America", "US" to "North America", "VI" to "North America",
        // South America
        "AR" to "South America", "BO" to "South America", "BR" to "South America", "CL" to "South America",
        "CO" to "South America", "EC" to "South America", "FK" to "South America", "GF" to "South America",
        "GY" to "South America", "PY" to "South America", "PE" to "South America", "SR" to "South America",
        "UY" to "South America", "VE" to "South America",
        // Oceania
        "AS" to "Oceania", "AU" to "Oceania", "CK" to "Oceania", "FJ" to "Oceania", "PF" to "Oceania",
        "GU" to "Oceania", "KI" to "Oceania", "MH" to "Oceania", "FM" to "Oceania", "NR" to "Oceania",
        "NC" to "Oceania", "NZ" to "Oceania", "NU" to "Oceania", "NF" to "Oceania", "MP" to "Oceania",
        "PW" to "Oceania", "PG" to "Oceania", "PN" to "Oceania", "WS" to "Oceania", "SB" to "Oceania",
        "TK" to "Oceania", "TO" to "Oceania", "TV" to "Oceania", "UM" to "Oceania", "VU" to "Oceania",
        "WF" to "Oceania",
        // Antarctica
        "AQ" to "Antarctica"
    )

    private val aliasToIso2 = mapOf(
        "UAE" to "AE",
        "UNITEDARABEMIRATES" to "AE",
        "SAUDIARABIA" to "SA",
        "KSA" to "SA",
        "BAHRAIN" to "BH",
        "QATAR" to "QA",
        "KUWAIT" to "KW",
        "OMAN" to "OM",
        "JORDAN" to "JO",
        "EGYPT" to "EG",
        "ALGERIA" to "DZ",
        "MOROCCO" to "MA",
        "TUNISIA" to "TN",
        "IRAQ" to "IQ",
        "LEBANON" to "LB",
        "LIBYA" to "LY",
        "SUDAN" to "SD",
        "YEMEN" to "YE",
        "PALESTINE" to "PS",
        "SYRIA" to "SY",
        "IRAN" to "IR",
        "USA" to "US",
        "UNITEDSTATES" to "US",
        "UNITEDSTATESOFAMERICA" to "US",
        "UK" to "GB",
        "UNITEDKINGDOM" to "GB",
        "GREATBRITAIN" to "GB",
        "RUSSIA" to "RU",
        "SOUTHKOREA" to "KR",
        "NORTHKOREA" to "KP",
        "VIETNAM" to "VN",
        "BOLIVIA" to "BO",
        "VENEZUELA" to "VE",
        "BOSNIAANDHERZEGOVINA" to "BA",
        "CZECHREPUBLIC" to "CZ",
        "TANZANIA" to "TZ",
        "SWAZILAND" to "SZ"
    )

    fun resolveRegion(country: String?): String? {
        val iso2 = resolveIso2(country) ?: return null
        return isoToRegion[iso2]
    }

    private fun resolveIso2(country: String?): String? {
        if (country.isNullOrBlank()) return null
        val cleaned = country.replace(Regex("[^A-Za-z]"), "").uppercase()
        if (cleaned.length == 2) {
            return cleaned
        }
        aliasToIso2[cleaned]?.let { return it }
        return null
    }
}
