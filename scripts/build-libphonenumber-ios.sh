#!/usr/bin/env bash
#
# Compiles the vendored libPhoneNumber-iOS into a static archive for one Apple target.
#
#   build-libphonenumber-ios.sh <sdk> <clang-target> <output-dir>
#   build-libphonenumber-ios.sh iphonesimulator arm64-apple-ios15.0-simulator build/libphonenumber/iosSimulatorArm64
#
# Invoked by the `buildLibPhoneNumber<Target>` tasks in ccp/build.gradle.kts, one per Kotlin/Native
# iOS target; the archive is then embedded into that target's cinterop klib.
#
# Three details matter, and each one fixes a failure that only shows up in a consumer's app:
#
#  * `-include ccp_libphonenumber_namespace.h` prefixes every global symbol (see that header), so a
#    host app that also links libPhoneNumber-iOS does not hit duplicate-symbol errors.
#  * `-fmodules` turns the framework imports into module imports, which records autolink entries
#    (-lz, -framework Contacts, -framework Foundation) in the object file. A *static* Kotlin
#    framework is linked by Xcode, not by Kotlin/Native, and without those entries every consumer
#    would have to add the linker flags by hand.
#  * The objects are merged into one relocatable object (`ld -r`) before archiving. The library
#    adds methods to NSArray through a category, and the linker only pulls archive members that
#    resolve an undefined *symbol* — a category defines none, so as a separate member it would be
#    dropped and the first call into it would crash with "unrecognized selector". One merged
#    member is pulled whole.
set -euo pipefail

if [ "$#" -ne 3 ]; then
  echo "usage: $0 <sdk> <clang-target> <output-dir>" >&2
  exit 64
fi

SDK="$1"
TARGET="$2"
OUT="$3"

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="${ROOT}/ccp/src/nativeInterop/libPhoneNumber-iOS"
NAMESPACE="${ROOT}/ccp/src/nativeInterop/cinterop/ccp_libphonenumber_namespace.h"
ARCH="${TARGET%%-*}"

SYSROOT="$(xcrun --sdk "${SDK}" --show-sdk-path)"
OBJ="${OUT}/obj"
rm -rf "${OUT}"
mkdir -p "${OBJ}"

for source in "${SRC}"/libPhoneNumber/*.m; do
  xcrun --sdk "${SDK}" clang -c \
    -target "${TARGET}" \
    -isysroot "${SYSROOT}" \
    -fobjc-arc \
    -fmodules \
    -Os \
    -Wno-everything \
    -include "${NAMESPACE}" \
    -I "${SRC}/libPhoneNumber" \
    -I "${SRC}/libPhoneNumberInternal" \
    "${source}" \
    -o "${OBJ}/$(basename "${source}" .m).o"
done

xcrun --sdk "${SDK}" ld -r -arch "${ARCH}" -o "${OUT}/libccpphonenumber.o" "${OBJ}"/*.o

# Every exported symbol must carry the CCP prefix. A class or constant added by a newer
# libPhoneNumber-iOS that is missing from ccp_libphonenumber_namespace.h would otherwise ship
# under its upstream name and reintroduce the duplicate-symbol clash the prefix exists to prevent.
# (Block descriptors and the NSCoding/NSCopying protocol records are linker-coalesced, not ours.)
LEAKED="$(nm -gU "${OUT}/libccpphonenumber.o" | awk 'NF == 3 { print $3 }' \
  | grep -vE '^_(CCP|ccp_)|^_OBJC_(CLASS|METACLASS|IVAR)_\$_CCP|^__block_descriptor|^__OBJC_(LABEL_)?PROTOCOL_\$_NS' \
  || true)"
if [ -n "${LEAKED}" ]; then
  echo "error: libPhoneNumber-iOS exports symbols without the CCP prefix:" >&2
  echo "${LEAKED}" | sed 's/^/  /' >&2
  echo "Add them to ccp/src/nativeInterop/cinterop/ccp_libphonenumber_namespace.h." >&2
  exit 1
fi

xcrun --sdk "${SDK}" libtool -static -o "${OUT}/libccpphonenumber.a" "${OUT}/libccpphonenumber.o"
rm -rf "${OBJ}" "${OUT}/libccpphonenumber.o"
