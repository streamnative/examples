#!/bin/bash


# List all folders in the current directory
for dir in */; do
    if [ -d "$dir" ]; then
        echo "Building ${dir%/}..."
        cd "$dir" && go build && cd ..
    fi
done
