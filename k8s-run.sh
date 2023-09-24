#!/bin/bash
cd "$(dirname "$0")" || (echo "Directory not found." && exit)
unset http_proxy
unset https_proxy
find "$PWD" -maxdepth 2 -type f -name "*.yaml" -exec kubectl apply -f {} \;
