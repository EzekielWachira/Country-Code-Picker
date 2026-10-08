// Renames every global symbol of the vendored libPhoneNumber-iOS so it cannot collide with a
// copy the host app links itself.
//
// Phone-number apps are exactly the apps likely to depend on libPhoneNumber-iOS directly (through
// SwiftPM or CocoaPods). Without this header both copies would define `_OBJC_CLASS_$_NBPhoneNumberUtil`
// and friends, which is a duplicate-symbol link error with a static framework and an undefined
// "which class wins" at runtime with a dynamic one. Prefixing makes the two copies unrelated.
//
// The header is force-included (`-include`) both when the Objective-C sources are compiled
// (scripts/build-libphonenumber-ios.sh) and when cinterop parses the headers (libPhoneNumber.def),
// so the Kotlin bindings see exactly the names the archive defines: `CCPNBPhoneNumberUtil`, etc.
//
// The list is every external symbol `nm -gU` reports for the compiled sources. When the vendored
// version is updated, re-run that check: a new class or constant that is not listed here would
// silently keep its unprefixed name.

#ifndef CCP_LIBPHONENUMBER_NAMESPACE_H
#define CCP_LIBPHONENUMBER_NAMESPACE_H

// Objective-C classes
#define NBAsYouTypeFormatter CCPNBAsYouTypeFormatter
#define NBMetadataHelper CCPNBMetadataHelper
#define NBNumberFormat CCPNBNumberFormat
#define NBPhoneMetaData CCPNBPhoneMetaData
#define NBPhoneNumber CCPNBPhoneNumber
#define NBPhoneNumberDesc CCPNBPhoneNumberDesc
#define NBPhoneNumberUtil CCPNBPhoneNumberUtil
#define NBRegExMatcher CCPNBRegExMatcher
#define NBRegularExpressionCache CCPNBRegularExpressionCache

// C constants
#define NB_NON_BREAKING_SPACE CCP_NB_NON_BREAKING_SPACE
#define NB_PLUS_CHARS CCP_NB_PLUS_CHARS
#define NB_REGION_CODE_FOR_NON_GEO_ENTITY CCP_NB_REGION_CODE_FOR_NON_GEO_ENTITY
#define NB_UNKNOWN_REGION CCP_NB_UNKNOWN_REGION
#define NB_VALID_DIGITS_STRING CCP_NB_VALID_DIGITS_STRING

// Generated metadata blob
#define kPhoneNumberMetaData ccp_kPhoneNumberMetaData
#define kPhoneNumberMetaDataCompressedLength ccp_kPhoneNumberMetaDataCompressedLength
#define kPhoneNumberMetaDataExpandedLength ccp_kPhoneNumberMetaDataExpandedLength

#endif
