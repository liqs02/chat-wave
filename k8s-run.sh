#!/bin/bash
cd "$(dirname "$0")" || (echo "Directory not found." && exit)
find "$PWD" -maxdepth 2 -type f -name "*.yaml" -exec kubectl apply -f {} \;
