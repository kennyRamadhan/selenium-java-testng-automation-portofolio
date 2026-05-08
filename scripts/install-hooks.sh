#!/bin/sh
# Install tracked git hooks for this repo.
# Copies .githooks/* into .git/hooks/ and makes them executable.

set -e

HOOKS_SRC=".githooks"
HOOKS_DST=".git/hooks"

if [ ! -d "$HOOKS_SRC" ]; then
    echo "ERROR: $HOOKS_SRC/ not found. Run from the repository root."
    exit 1
fi

if [ ! -d "$HOOKS_DST" ]; then
    echo "ERROR: $HOOKS_DST/ not found. Is this a git repo?"
    exit 1
fi

for hook in "$HOOKS_SRC"/*; do
    name=$(basename "$hook")
    cp "$hook" "$HOOKS_DST/$name"
    chmod +x "$HOOKS_DST/$name"
    echo "Installed: $HOOKS_DST/$name"
done

echo ""
echo "Git hooks installed."
