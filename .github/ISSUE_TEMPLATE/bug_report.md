---
name: Bug report
about: Create a report to help us improve
title: ''
labels: ''
assignees: ''

---

Describe What happened
===

A clear and concise description of what the bug is.

How To Reproduce
===

Steps to reproduce the behavior:

1. What did you wish to do?
---

2. How did you configure build? Add your `build.gradle` file(or other `gradle` build files) .
---

3. What task did you run?
---

4. What is an error or exception?
---

Expected behavior
===

A clear and concise description of what you expected to happen.

StackTraces or Console logs
===

Add stacktrace or console logs to help explain your problem.
---

(Run tasks with `--stacktrace` option, if the gradle task fails)

Environment
===

- OS: [e.g. Ubuntu 20.04 / MacOS X 10.15.5 / Windows10]
- Plugin Version [e.g. v0.7.1...]
- Gradle Version [e.g. 6.5.1...]
- Java Version(GraalVM version) [e.g. GraalVM CE 20.1.0 (build 11.0.7+10)]

Additional context
===

Add any other context about the problem here.
If it is about `generateNativeImageConfig` task or `mergeNativeImageConfigTask`, please provide generated config file(if available, it locates under `build/tmp/native-image-config/out-n` directory).
