# Sorcerer Explained

**Table of Contents**

- [Overview](#)
- [Initialization](#)
- [Task](#)
- [Pipeline](#)
- [Module](#)
- [Execution](#)
- [Persistence Layer](#)


## Overview

Sorcerer is a workflow scheduler that is designed to be easily extensible and modular.

## [Initialization](initialization.md)

There are 3 steps to the initialization of the Sorcerer instance:

1. Process configuration files
2. Process class annotations
3. Reconciliation and registration

#### Configuration Files
First, Sorcerer processes all given configuration files and checks for configuration syntax correctness as well as converts them into internal objects.

#### Annotations
Next, Sorcerer processes relevant annotations in the java classpath (or packages if defined). It will search for annotations and then ensure that the class implements the required interface to be used by Sorcerer (i.e. `Task.class` or `Pipeline.class`).


#### Reconciliation
After configuration files and annotations are processed, Sorcerer eagerly attempts to reconcile all the relevant configured objects with their corresponding classes. For example, a task that is defined in the configuration files should also have a implementation in the Java packages.

For more details go to the [Initialization](initialization.md) page.

## [Task](task.md)
Defining and implementing tasks require two steps:

1. Defining task in configuration files (See [Task Configuration](task.md#Configuration))
2. Implementing `Task.class` with `@SorcererTask` annotation (See [Task Implementation](task.md#Implementation))

For more information see the [Task](task.md) page.

## [Pipeline](pipeline.md)

Pipelines must be defined but implementing a specific instance is optional.

1. Defining pipeline in configuration files (See [Pipeline Configuration](pipeline.md#Configuration))
2. [Optional] Implementing `Pipeline.class` with `@SocererPipeline` annotation (See [Pipeline Implementation](pipeline.md#Implementation))

For more information see the [Pipeline](pipeline.md) page.

## [Module](module.md)
A module defines the content and context of a single instance of Sorcerer. Or more specifically, it is the configuration of the instance of Sorcerer running in a single JVM. It has the following configuration fields:

- Name
- Pipelines
- [Email](module.md#module-email)
- Storage
- Packages

For more information see the [Module](module.md) page.

## [Execution](execution.md)

After Sorcerer is initialized, it schedules all the pipelines in a module.

For more information see the [Execution](execution.md) page.

## [Persistence Layer](persistence.md)

Sorcerer relies on a persistent storage layer to maintain pipeline and task states. Out of the box Sorcerer can use HDFS, MySQL or Zookeeper as its persistence layer. Additionally, custom storage layers can be implemented by implementing `StatusStorage.class`.

For more information see the [Persistence Layer](persistence.md) page.

## API

For more information see the Javadocs.
