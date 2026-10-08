#!/usr/bin/env bash
#
# Referenced by jitpack.yml (`before_install`). JitPack checks the repository out into a clean
# container where local.properties does not exist — that file is developer-local and correctly
# git-ignored — so the Android Gradle Plugin cannot find the SDK and the build fails during
# configuration with "SDK location not found".
#
# The JitPack Android images export ANDROID_HOME (older ones ANDROID_SDK_ROOT). Writing it into
# local.properties is the one thing AGP will read on every path.
set -euo pipefail

SDK_DIR="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"

if [ -z "${SDK_DIR}" ]; then
  echo "prepareJitpackEnvironment: neither ANDROID_HOME nor ANDROID_SDK_ROOT is set." >&2
  echo "The Android SDK is required to build the :ccp library module." >&2
  exit 1
fi

echo "sdk.dir=${SDK_DIR}" > local.properties
echo "prepareJitpackEnvironment: wrote sdk.dir=${SDK_DIR} to local.properties"

# JitPack publishes whatever `publishToMavenLocal` produces, and the Vanniktech plugin signs
# release publications when RELEASE_SIGNING_ENABLED is set. No GPG key exists in the JitPack
# container, so signing is disabled there (the build script already guards on signingInMemoryKey,
# this makes the intent explicit for anyone reading the JitPack log).
echo "RELEASE_SIGNING_ENABLED=false" >> gradle.properties
