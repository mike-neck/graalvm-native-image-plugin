#!/usr/bin/env bash

readonly githubToken="${TOKEN}"
readonly gradleVersion="${GRADLE_VERSION}"

readonly latestVersion=$(
  curl --request GET -sL \
    --url  'https://api.github.com/repos/mike-neck/graalvm-native-image-plugin/tags?per_page=1'  \
    -H 'accept:application/vnd.github.v3+json' \
    -H "authorization: ${githubToken}" | \
  jq -r '.[] | .name'
)

readonly testDirectory="version-tests"
readonly originalSource="src/functionalTest/resources/config-project"

mkdir -p "${testDirectory}/src/main/java/com/example"
mkdir -p "${testDirectory}/src/main/resources/META-INF/services"

readonly pattern=$(echo "${originalSource}/" | sed -e 's/\//\\\//g')
for sourceFile in $(find "${originalSource}"); do
  if [ -d "${sourceFile}" ]; then
      continue
  fi
  destination=$(
    echo "${sourceFile}" |
      sed -e "s/${pattern}//g" -e "s/\.txt$//g" |
      tr '_' '/' |
      tr '-' '.' |
      sed -e "s/\.\./-/g"
  )
  printf "%s -> %s\n" "${sourceFile}" "${destination}"
  cp "${sourceFile}" "${testDirectory}/${destination}"
done

mv "${testDirectory}/build.gradle" "${testDirectory}/build.gradle-tmp"
cat "${testDirectory}/build.gradle-tmp" |
  sed "s/graalvm-native-image'/graalvm-native-image' version '${latestVersion}'/g" >"${testDirectory}/build.gradle"
rm "${testDirectory}/build.gradle-tmp"

cp -r ./gradle "${testDirectory}/gradle"
mv "${testDirectory}/gradle/wrapper/gradle-wrapper.properties" "${testDirectory}/gradle/wrapper/gradle-wrapper.properties-tmp"
cat "${testDirectory}/gradle/wrapper/gradle-wrapper.properties-tmp" | \
  sed -e  "s/[0-9]\{1,\}\.[0-9]\{1,\}\.\([0-9]\{1,\}\)\{0,\}-all/${gradleVersion}-bin/g" > \
  "${testDirectory}/gradle/wrapper/gradle-wrapper.properties"
rm "${testDirectory}/gradle/wrapper/gradle-wrapper.properties-tmp"

cp gradlew "${testDirectory}/"

cd "${testDirectory}"
./gradlew nativeImage
