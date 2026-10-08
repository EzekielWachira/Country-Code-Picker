// Umbrella header cinterop binds (see libPhoneNumber.def).
//
// The renames come first so the four libPhoneNumber-iOS headers below are parsed with the same
// CCP-prefixed names the archive was compiled with (scripts/build-libphonenumber-ios.sh
// force-includes the same file). The Kotlin bindings therefore name CCPNBPhoneNumberUtil & co.,
// which is what the archive defines.
#import "ccp_libphonenumber_namespace.h"

#import "NBPhoneNumberDefines.h"
#import "NBPhoneNumber.h"
#import "NBPhoneNumberUtil.h"
#import "NBAsYouTypeFormatter.h"
