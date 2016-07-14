hyper-build-step-plugin
=======================

This plugin allows to add "Execute shell in Hyper_" build step into your job.

# Features
Plugin currently support following features:

- Install hyper cli
- Set Hyper_ credentials
- Support "Execute shell in Hyper_" build step in to job

# Build

## Compile
```
$ mvn compile
```

## Test
```
//compile + test
$ mvn test
```

## Package

> **output**: target/hyper-build-step-plugin.hpi

```
//compile + test + package
$ mvn package

//compile + package
$ mvn package -DskipTests
```

## Install

> **target**: ~/.m2/repository/hyper-build-step/hyper-build-step-plugin/1.0-SNAPSHOT/hyper-build-step-plugin-1.0-SNAPSHOT.hpi

```
//compile + test + package + install
$ mvn install

//compile + package + install
$ mvn install -DskipTests
```
