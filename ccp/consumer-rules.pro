# Rules applied automatically to every app that depends on this library.
#
# Consumers should not have to discover any of this from a crash in a minified release build,
# so everything the library needs at runtime under R8 is declared here rather than in the
# README.

# --- libphonenumber -------------------------------------------------------------------
#
# libphonenumber keeps its region metadata in generated classes that are loaded reflectively
# by name (MetadataManager builds "<prefix>_<countryCode>" and calls Class.forName), so R8
# sees no reference to them and strips the lot. The symptom is not a link error but wrong
# behaviour: every number silently fails to parse, because the metadata loader throws and is
# swallowed. Keeping the metadata packages and the loader interfaces avoids that.
-keep class com.google.i18n.phonenumbers.metadata.** { *; }
-keep class com.google.i18n.phonenumbers.buildtools.** { *; }
-keepclassmembers class com.google.i18n.phonenumbers.** {
    public static ** getInstance();
}
-keepnames class com.google.i18n.phonenumbers.MetadataLoader
-keepnames class com.google.i18n.phonenumbers.metadata.DefaultMetadataDependenciesProvider

# The generated metadata lives in resources, not classes, but the *loader* resolves it by
# package name. Losing these names breaks the lookup path.
-keeppackagenames com.google.i18n.phonenumbers.**

# libphonenumber compiles against a handful of javax.annotation types that are not present on
# Android. They are only used at compile time; silence the warning rather than making every
# consumer add this themselves.
-dontwarn javax.annotation.**
-dontwarn com.google.i18n.phonenumbers.**$$Lambda$*

# --- Library public API ---------------------------------------------------------------
#
# The public surface is what consumers call, so it must survive obfuscation with usable names
# for stack traces. Enum values are additionally read by name through valueOf() when state is
# restored from a saved Bundle (see CountryPickerStateSaver / PhoneNumberFieldState).
-keepclassmembers enum com.ezzy.ccp.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Data classes restored by rememberSaveable go through their constructors only; no reflection
# is involved, so no keep rule is needed for them. Kotlin metadata, however, must survive for
# consumers that inspect the API reflectively (e.g. Compose tooling in a minified build).
-keep class kotlin.Metadata { *; }
