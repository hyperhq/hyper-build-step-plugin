hyper-build-step-plugin
=======================

Run jenkins job in Hyper_ container.
This plugin allows to add "Execute shell in Hyper_" build step into your job.

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Use plugin](#use-plugin)
	- [Prerequisites](#prerequisites)
	- [Install plugin by manually](#install-plugin-by-manually)
	- [Config Hyper_](#config-hyper)
	- [Config job](#config-job)
	- [View build result](#view-build-result)
- [Build plugin](#build-plugin)
	- [Prerequisites](#prerequisites)
	- [Compile](#compile)
	- [Test](#test)
	- [Package](#package)
	- [Install](#install)

<!-- /TOC -->

# Use plugin

## Prerequisites

- Jenkins
- [hyper-commons-plugin](https://github.com/hyperhq/hyper-commons-plugin)

## Install plugin by manually

open Jenkins Web UI in web browser

get pre-build `hyper-build-step-plugin.hpi` [here](target/hyper-build-step-plugin.hpi)

```
Manage Jenkins -> Manage Plugins -> Advanced -> Upload Plugin
```

![](images/upload-plugin.PNG)


## Config Hyper_

- install `hyper-commons-plugin` first
- login https://console.hyper.sh, then create credentials.

```
Config -> Configure System
  -> Hyper Config : set AccessKey and SecretKey
  -> Test connection
  -> Download Hypercli
```

![](images/config-hyper-commons-plugin.PNG)


## Config job

```
Config -> Build -> Execute shell in Hyper_
```
![](images/config-job.PNG)

## View build result

- click `Build Now` to start build job by manually
- click `Console Output` to view build result

![](images/view-result.PNG)


# Build plugin

## Prerequisites

- java 1.7+
- maven 3+

## Compile
```
$ mvn compile
```

## Test

compile + test

```
$ mvn test
```

## Package

> **output**: target/hyper-build-step.hpi

compile + test + package

```
$ mvn package

//skip test
$ mvn package -DskipTests
```

## Install

compile + test + package + install

```
$ mvn install

//skip test
$ mvn install -DskipTests
```
