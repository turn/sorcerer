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


## [Initialization](initialization.md)

There are 3 steps to the initialization of the Sorcerer instance:

1. Process configuration files
2. Process class annotations
3. Reconciliation and registration

#### Configuration Files
First, Sorcerer processes all given configuration files and checks for configuration syntax correctness as well as converts them into internal objects.

#### Annotations
Next, Sorcerer processes relevant annotations in the java classpath (or packages if defined). It will search for annotations and then ensure that the class implements the required interface to be used by Sorcerer (i.e. `Task.class` or `Pipeline.class`)


#### Reconciliation
After configuration files and annotations are processed, Sorcerer eagerly attempts to reconcile all the relevant configured objects with their corresponding classes. For example, a task that is defined in the configuration files should also have a implementation in the Java packages.
	
For more details go to the [Initialization](initialization.md) page
	
## [Task](task.md)
Task 
Fork, join, class?, oozie?
	Configuration
	Implementation
		Execution flow
		Dependency
			HDFSPath
			Task
			Time

## [Pipeline](pipeline.md)


## [Module](module.md)
A module defines the content and context of a single instance of Sorcerer. Or more specifically, it is the configuration of the instance of Sorcerer running in a single JVM. It has the following configuration fields:

- Name
- Pipelines
- [Email](module.md#Email)
- Storage
- Packages

## [Execution](execution.md)

## [Persistence Layer](persistence.md)

