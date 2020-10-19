#!/usr/bin/env bash

set -eu

readonly yj="${YJ}"
readonly token="${TOKEN}"

curl -s\
    "https://api.github.com/repos/gradle/gradle/releases?per_page=40" \
    -H 'accept: application/vnd.github.v3+json' \
    -H "authorization: token ${token}" |
./script/versions.jq |
"${yj}" -jy |
tee .github/workflows/versions.yml
