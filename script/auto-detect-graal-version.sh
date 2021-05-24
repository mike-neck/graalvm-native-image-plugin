#!/usr/bin/env bash

if [[ -z "${GH_TOKEN}" ]]; then
  echo "error: GH_TOKEN is empty"
  exit 1
fi

# shellcheck disable=SC1009
curl -s "https://api.github.com/repos/graalvm/graalvm-ce-builds/releases" \
  --header 'accept: application/vnd.github.v3+json' \
  --header "Authorization: token ${GH_TOKEN}" \
| jq '
      .[] |
      {
        tag: .tag_name,
        date: .created_at,
        name: .name ,
        version: .tag_name | sub("vm-"; ""),
        items: [.assets[] | .name | split("-") | .[] | select(contains("java")) ] | unique
      }' \
| tee ./script/data/graal-version.jsonl
