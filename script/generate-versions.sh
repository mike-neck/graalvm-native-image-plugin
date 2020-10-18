#!/usr/bin/env bash

curl -s\
    "https://api.github.com/repos/gradle/gradle/releases?per_page=40" \
    -H 'accept: application/vnd.github.v3+json' \
    -H "authorization: token ${TOKEN}" |
./script/versions.jq |
yj -jy > .github/workflows/versions.yml

cat .github/workflows/versions.yml
