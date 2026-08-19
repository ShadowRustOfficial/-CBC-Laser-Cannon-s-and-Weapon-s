#!/bin/sh
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle was not found on PATH. Install Gradle or use an IDE with Gradle integration." >&2
  exit 1
fi
exec gradle "$@"
