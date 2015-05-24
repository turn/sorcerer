# Sorcerer

Built by [Turn](https://www.turn.com)

**Sorcerer** is a workflow scheduler and manager framework developed at Turn. It is built to be extendable, not tightly coupled to any system (i.e. Hadoop, MySQL, etc.).

## Features

- Built to be easily extendable and pluginable
- Schedules and manages workflows
- Simple yaml-based configuration
- Email alerts to admins
- Cron scheduling
- Modular workspaces
- Adhoc runs of workflows

### Coming soon...
- Web UI
- Oozie
- SLA alerting
- Timeout locks
- Workflow Recoverability
- DAG cycle detection

Sorcerer is designed to be as generic as possible, able to run any kind of task on any compatible system.

## Installation

### Dependencies
Java 6+ is required. Sorcerer also uses maven as its dependency manager so maven should be installed. Maven should take care of the rest.

### Building

To build Sorcerer packages from source run in the Sorcerer root directory:

```
mvn package
```

The executable jars (with and without dependencies) will be placed in the `/target` directory.


## Documentation

Coming soon!

## Quick Start

### Configuration

There are four required configuration objects: [module](#Module), [pipeline](#Pipeline), [task](#Task), and [task classes](#Task_Classes). Currently configuration files should only be written in YAML while the task classes should be written in java

#### Module

For each instance of sorcerer there can only be one module definition. A module defines the content and context of the instance of sorcerer (i.e. name, pipelines to run, admins, persistence layer, etc.)


##### Example module configuration

```YAML
!module
	name: my_first_module
	
	# pipelines to run
	pipelines:
		- new_pipeline

	# persistence layer
	storage:
		!hdfs
			root: /hdfs/status/dir/
			
	# packages to scan for task classes
	packages:
		- java.packages.to.scan

	# email configuration
	email:
		!email
			enabled: true
			host: email.server
			admin: email@example.com
	
```
For more detailed examples see [Documentation](#Documentation)

#### Pipeline

A pipeline is a workflow of tasks defined by specifying the initial task in the workflow. 



##### Example pipeline configuration
```YAML
!pipeline
	name: new_pipeline
	
	# Initial task name
	init: new_task
	
	# Interval in seconds between pipeline attempts
	interval: 10
	
	# Previous iterations to attempt scheduling
	lookback: 1
---
...
```
For more detailed examples see [Documentation](#Documentation)

#### Tasks

A task is essentially the smallest unit of action in a sorcerer workflow, basically a node in the workflow DAG. Each task is definied by a unique name and also the next tasks in the workflow. A task with no next tasks defined is considered a terminal task. By default each task will not be scheduled until all tasks that specifies it as next are successfully completed.

##### Example task configuration
```YAML
# initial task
!task
    name: new_task
    next:
        - next_task_1
        - next_task_2
---
...
```
For more detailed examples see [Documentation](#Documentation)

#### Task classes

In order for Sorcerer to know what to execute for a task, each task requires a corresponding class that implements the `Task` class (with the exception of fork and join tasks). Additionally the class must be mapped to the task by the `@SorcererTask(name)` annotation where the `name` field equals its corresponding name in the configuration files.

##### Example Task class
```java
package com.example.tasks;

import com.turn.sorcerer.task.SorcererTask;

@SorcererTask(name = "new_task")
public class NewTask implements Task {

	@Override
	public void init(Context context) {
		// Some initialization code
	}

	@Override
	public void exec(Context context) throws Exception {
		System.out.println("Running a new task!");
	}


	@Override
	public Collection<Dependency> getDependencies(int iterNo) {
		return null;
	}
}
```
For more detailed examples see [Documentation](#Documentation)

### Starting Sorcerer

Sorcerer provides a builder to specify the configuraion files path as well as add and packages to scan for task classes.

```java
	Sorcerer sorcerer = Sorcerer.builder()
			.addConfigPath("path/to/configuration/files")
			.addPackage("package.containing.tasks")
			.create();
```
Once the sorcerer instance is created, it can be started and stopped by calling:

```
sorcerer.start()
...
sorcerer.stop()

```


## Authors and Maintainers
- Tommy Shiou
- Margaret Zhang
- Arjun Satish
