#!/usr/bin/env bash

set -eu

level=$1

if [[ "${level}" == "major" ]]; then
    echo "major release"
elif [[ "${level}" == "minor" ]]; then
    echo "minor release"
elif [[ "${level}" == "patch" ]]; then
    echo "patch release"
else
    echo "./release.sh [major | minor | patch]"
    exit 1
fi

current_branch=$(git branch | grep '\*' | awk '{print $2}')

git fetch
branch_name=$(instant -f unix) # instant can be found at https://github.com/mike-neck/instant

echo "create new branch: release/${branch_name}"
git checkout -b "release/${branch_name}" origin/master

git commit --allow-empty -m "release/${level}"
gh pr create \
  --base master \
  --title "release/${level}" \
  --body "release/${level}" 

git checkout "${current_branch}"
git branch -D ${branch_name}
