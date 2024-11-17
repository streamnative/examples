#!/bin/bash

# Search and delete Linux/macOS binaries (files with no extension and executable permissions)
echo "Searching for Go binaries in the current directory"

# For macOS/BSD and Linux: Check if a file has executable permissions without relying on -perm /a+x
find . -type f ! -name '*.*' -exec sh -c 'if [ -x "$1" ]; then echo "Deleting: $1"; rm "$1"; fi' _ {} \;

