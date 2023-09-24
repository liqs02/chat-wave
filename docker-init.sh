#!/bin/bash
cd "$(dirname "$0")" || (echo "Directory not found." && exit)

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Usage: $0 <version> [services]"
    exit 1
fi

version="$1"

if [ "$2" = "all" ]; then
    services=("registry" "gateway" "auth-service" "account-service" "chat-service")
else
    shift
    services=("$@")
fi

for dir in "${services[@]}"; do
    imageName="${dir//-/}"
    docker build -t "patryklikus/chatwave-$imageName:$version" "$dir" &&
    docker push "patryklikus/chatwave-$imageName:$version"
done
