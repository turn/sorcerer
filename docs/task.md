<!---
  Copyright (c) 2015, Turn Inc. All Rights Reserved.
  Use of this source code is governed by a BSD-style license that can be found
  in the LICENSE file.
-->

# Sorcerer Task

[Back to Documentation](README.md)

## <a name="Configuration"></a>Configuration

Tasks are defined in the configuration files and then Sorcerer deserializes the YAML fields into an internal task object. To mark this, task definitions should be specified with a `!task` tag and delimited with `---`. The task fields are:

key  |required?|description
-----|---------|:-----------
name |yes      |task name unique in the task namespace
next |yes      |list of task names that are next in workflow
exec |no       |see [Provided task types](#)


### <a name="Provided_task_types"></a>Provided task types

There are some provided task types that do not require you to implement. They are specified by populating the `exec` field in the task definition.

- #### fork
  This is a placeholder task that will inform Sorcerer to launch all of its `next` tasks in the next scheduled attempt of the pipeline.


- #### join
  Since Sorcerer by default will not execute a task until all tasks that specify the current one as `next` are completed, any specified task is technically a "join" task. Nonetheless a workflow definition may sometimes be more readable to have a explicit join task. Therefore we provide a placeholder task that will wait until all previous tasks complete successfully before it is scheduled.

##### Examples
```YAML
!task
	name: task_1
	next:
		- task_2
		- task_3
	exec: fork
---
!task
	name: task_2
	...
```


## <a name="Implementation"></a>Implementation

The Task implementation has two parts: Annotation and Methods

### <a name="Annotation"></a>Annotation

Sorcerer task implementation should be annotated with the `@SorcererTask` annotation with the `name` field populated. The name specified in the annotation will be used to map to the corresponding task definition in the configuration files.

```
@SorcererTask(name = "task_name")
```

### <a name="Methods"></a>Methods

The `Task` class is an interface that requires the user to override and implement the following methods:

- #### `init(Context context)`
  Some tasks will require some initialization logic before execution. Since we use Guice injections to back instance creation, you could also implement some of your initialization code in a constructor.

  Similiarly, the `init()` method is provided to implement the task initialization code. It also provides a Context object which contains some parameters (like iteration number) as well as provides a way to pass parameters from the init method to the rest of the task without having to implement class variables.

  It is important to note that the `init()` method is called **before** before the call to `getDependencies()`. This can sometimes be useful becuase sometimes the dependency checking step can also require initialization. However the user should be aware of this because if the initialization step itself has dependencies, the user will have to implement those checks.

- #### `getDependencies(int iterNo)`

  This method returns a Collection of `Dependency` instances. These dependencies have to all resolve to true before Sorcerer proceeds with scheduling the task

- #### `exec(Context context)`

  This is the main method of the task. The actual business logic of the task should be put into method. The method also throws Exception so any exceptions thrown in the method will be caught, then wrapped in a SorcererException and logged.


##### Examples
```java
package com.example.tasks;

import com.turn.sorcerer.task.SorcererTask;

@SorcererTask(name = "new_task")
public class NewTask implements Task {

	private int scheduledHour;
	private int scheduledMinute;

    @Override
    public void init(Context context) {
        scheduledHour = 17;
        scheduledMinute = 30;
    }

    @Override
    public void exec(Context context) throws Exception {
        System.out.println("Running a new task!");
    }


    @Override
    public Collection<Dependency> getDependencies(int iterNo) {
    	return ImmutableList.<Dependency>builder()
				.add(new SorcererTaskDependency("prev_task_1"))
				.add(new TimeDependency(new DateTime()
						.withHourOfDay(scheduledHour)
						.withMinuteOfHour(scheduledMinute)))
				.build();
    }
}
```

## <a name="Dependency"></a>Dependency

The `Dependency` class is the object used by Sorcerer to check that a task's dependency is fulfilled. It only has one method to override:

- ##### `check(int iterNo)`

  This method returns true if the dependencies are fulfilled. The iteration number is provided to be used in the dependency checking.

### <a name="Provided_Dependency_Implementations"></a>Provided Dependency implementations

There are some Dependency implementations already made available for some common task dependencies.

#### SorcererTaskDependency

This is a task dependency on another Sorcerer task. This can be used if a task is dependent on another task from the same or another pipeline.It provides a variety of ways to specify the task and/or iteration number. See the java docs for more details.

#### TimeDependency

This represents a task dependency based on time. This translates to a task being run only once a day. Sorcerer uses joda-time to represent time.

#### HDFSPathDependency

This is an abstract class that provides a `getPaths()` method where it will check for the existence of a the provided paths.

## <a name="Context"></a>Context

In order to provide some context information for the task, a `Context` object is provided. The Context object contains fields useful for the task to both get and provide information outside the context of the task.

- #### `getIterationNumber()`

  This will return an immutable `int` representing the current iteration number of the task.

---

[Back to Documentation](README.md)
