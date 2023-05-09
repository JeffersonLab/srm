# srm [![CI](https://github.com/JeffersonLab/srm/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/srm/actions/workflows/ci.yml) [![Docker](https://img.shields.io/docker/v/jeffersonlab/srm?sort=semver&label=DockerHub)](https://hub.docker.com/r/jeffersonlab/srm)
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application for managing system readiness at Jefferson Lab built with the [Smoothness](https://github.com/JeffersonLab/smoothness) web template.

![Screenshot](https://github.com/JeffersonLab/srm/raw/main/Screenshot.png?raw=true "Screenshot")

---
- [Overview](https://github.com/JeffersonLab/srm#overview)
- [Quick Start with Compose](https://github.com/JeffersonLab/srm#quick-start-with-compose)
- [Install](https://github.com/JeffersonLab/srm#install)
- [Configure](https://github.com/JeffersonLab/srm#configure)
- [Build](https://github.com/JeffersonLab/srm#build)
- [Release](https://github.com/JeffersonLab/srm#release)
---

## Overview
The System Readiness application allows machine operators and management to determine machine readiness.  The app inventories all systems of the machine and organizes systems into a hierarchy of categories.  Further, systems are assigned individual components to increase readiness specificity.  To aid in answering the more common question of where can beam be sent, each component is associated with zero or more beam destinations.  Component readiness is determined by System Responsible Groups, and each component has an ordered set assigned.   Each Responsible Group is associated with a set of group leaders, who are authorized to mark final readiness.

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/srm
cd srm
```
2. Launch [Compose](https://github.com/docker/compose)
```
docker compose up
```
3. Navigate to page
```
http://localhost:8080/srm
```

**Note**: Login with demo username "tbrown" and password "password".

See: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install
This application requires a Java 11+ JVM and standard library to run, plus a Java EE 8+ application server (developed with Wildfly).


1. Install service [dependencies](https://github.com/JeffersonLab/srm/blob/main/deps.yml)
2. Download [Wildfly 26.1.3](https://www.wildfly.org/downloads/)
3. [Configure](https://github.com/JeffersonLab/srm#configure) Wildfly and start it
4. Download [srm.war](https://github.com/JeffersonLab/srm/releases) and deploy it to Wildfly
5. Navigate your web browser to [localhost:8080/srm](http://localhost:8080/srm)

## Configure

### Configtime
Wildfly must be pre-configured before the first deployment of the app. The [wildfly bash scripts](https://github.com/JeffersonLab/wildfly#configure) can be used to accomplish this. See the [Dockerfile](https://github.com/JeffersonLab/srm/blob/main/Dockerfile) for an example.

### Runtime
Uses the [Smoothness Environment Variables](https://github.com/JeffersonLab/smoothness#environment-variables).

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/srm
cd srm
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

See: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

### Release
1. Bump the version number and release date in build.gradle and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
2. Create a new release on the GitHub Releases page corresponding to the same version in the build.gradle. The release should enumerate changes and link issues. A war artifact can be attached to the release to facilitate easy installation by users.
3. Build and publish a new Docker image [from the GitHub tag](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#8-build-an-image-based-of-github-tag). GitHub is configured to do this automatically on git push of semver tag (typically part of GitHub release) or the [Publish to DockerHub](https://github.com/JeffersonLab/srm/actions/workflows/docker-publish.yml) action can be manually triggered after selecting a tag.
4. Bump and commit quick start [image version](https://github.com/JeffersonLab/srm/blob/main/docker-compose.override.yml)
