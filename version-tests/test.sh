#!/usr/bin/env bash

set -eu

readonly gradleVersion="${GRADLE_VERSION}"
echo "test on ${gradleVersion}"

./gradlew replaceJar

readonly testDirectory="version-tests"
readonly originalSource="src/functionalTest/resources/config-project"

mkdir -p "${testDirectory}/src/main/java/com/example"
mkdir -p "${testDirectory}/src/main/resources/META-INF/services"

readonly pattern=$(echo "${originalSource}/" | sed -e 's/\//\\\//g')

while IFS= read -r -d '' sourceFile
do
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
done < <(find "${originalSource}" -print0)

mv "${testDirectory}/build.gradle" "${testDirectory}/build.gradle-tmp"

cat <<EOF > "${testDirectory}/build.gradle"                                        
buildscript {
  dependencies {
    classpath files('../build/libs/graalvm-native-image-plugin-snapshot.jar')
  }
}
apply plugin: 'java'
apply plugin: 'org.mikeneck.graalvm-native-image'
EOF
tail -n +5 "${testDirectory}/build.gradle-tmp" >> "${testDirectory}/build.gradle"
rm "${testDirectory}/build.gradle-tmp"

cp -r ./gradle "${testDirectory}/gradle"
mv "${testDirectory}/gradle/wrapper/gradle-wrapper.properties" "${testDirectory}/gradle/wrapper/gradle-wrapper.properties-tmp"
# shellcheck disable=SC2002
cat "${testDirectory}/gradle/wrapper/gradle-wrapper.properties-tmp" | \
  sed -e  "s/[0-9]\{1,\}\.[0-9]\{1,\}\.\([0-9]\{1,\}\)\{0,\}-all/${gradleVersion}-bin/g" > \
  "${testDirectory}/gradle/wrapper/gradle-wrapper.properties"
diff "${testDirectory}/gradle/wrapper/gradle-wrapper.properties-tmp" "${testDirectory}/gradle/wrapper/gradle-wrapper.properties"
rm "${testDirectory}/gradle/wrapper/gradle-wrapper.properties-tmp"

cp gradlew "${testDirectory}/"
echo "rootProject.name = '${PWD##*/}'" > "${testDirectory}/settings.gradle"

cd "${testDirectory}" || exit 
./gradlew nativeImage
