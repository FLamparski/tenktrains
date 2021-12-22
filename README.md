# 10k Trains

An attempt to derive metrics from train departure boards. Maybe that will actually
be useful to somebody.

![](docs/arch-uml-swimlanes.png)

This only looks like microservices, everything is in one app. However the different
modules will talk to each other using the application event bus like it's kafka.

## Setup

1. Download [`apache-cxf-3.4.5`][cxf] and extract it into `./apache-cxf-3.4.5`
2. Regenerate the client using the `:generateCodeFromWsdl` task
3. Copy `application-local.sample.yaml` to `application-local.yaml`
4. Update your LDB API token in `application-local.yaml`
5. Make sure you run the app locally with the Spring profile set to `local`

[cxf]: http://cxf.apache.org/download.html

## Local run

This project contains a `Boot Run` configuration for IntelliJ Community Edition
that sets the `local` profile and uses the `:bootRun` task. Otherwise, try:

```
# PowerShell, Windows
$env:SPRING_PROFILES_ACTIVE="local"; .\gradlew.bat :bootRun

# Bash
SPRING_PROFILES_ACTIVE=local ./gradlew :bootRun
```

## Integration tests

Integration tests are placed in a separate source set - `test` is for purely in-memory unit tests,
`integrationTest` is for anything that needs to spin up a spring context and/or containers.

Speaking of - integration tests use [Testcontainers][testcontainers]. These assume a local Docker,
which should Just Work in Linux, and also Just Work on MacOS if you have Docker for Mac.

On Windows, there are some steps that assume Docker for Windows outlined in the docs:
https://www.testcontainers.org/supported_docker_environment/windows/. For Podman/WSL2, watch
this space.

Running the integration tests requires no other special tools and can be done through Gradle using
your IDE or on the command line. IntelliJ works absolutely fine here.

[testcontainers]: https://www.testcontainers.org/
