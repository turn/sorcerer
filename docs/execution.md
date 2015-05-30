<!---
  Copyright (c) 2015, Turn Inc. All Rights Reserved.
  Use of this source code is governed by a BSD-style license that can be found
  in the LICENSE file.
-->

# Sorcerer Execution

[Back to Documentation](README.md)

## Starting and Stopping

Once Sorcerer is initialized (See [Initialization](#initialization.md)), the service can be started by simply calling the `start()` method. Once start is called, Sorcerer will start scheduling all pipelines in the module.

```java
// Initializing Sorcerer
Sorcerer sorcerer = Sorcerer.builder()
  .addConfigPath(/sorcerer/config/)
  .build()

// Starting Sorcerer  
sorcerer.start()
...
// Stopping Sorcerer
sorcerer.stop()
```

When you want to bring down Sorcerer you can gracefully do so by calling the sorcerer.stop() method. This will shutdown all of the Sorcerer threads.

## <a name="Pipelines"></a>Pipelines

Running pipelines consists of two types of threads, one that schedules pipelines, and one that executes it. There is one thread that exists to schedule a pipeline type, and then for each specific instance of a pipeline running, a thread will be spawned to launch and monitor the instance of pipeline. The actual scheduling and execution of the tasks will be pushed down to the Task level (see [Tasks](#Tasks) section).

### Scheduling
Once Sorcerer starts, it will launch a single thread that will essentially be the master thread of the pipeline. This thread's job is at a frequency defined by the [pipeline interval](pipeline.md#Configuration), it will:

1. Get a list of iterations to schedule of the pipeline. This is defined by the range of numbers between the current iteration number ( [`Pipeline.getCurrentIterationNumber()`](pipeline.md#Methods)) and the defined [pipeline lookback](pipeline.md#Configuration) value.
2. Flag any currently running pipelines that are outside the range of iterations to run (from step 1) to be shut down.
3. Create any instances of pipelines for iteration numbers that aren't already scheduled.
4. For each instance of pipeline that is scheduled, create a thread to execute the pipeline if it is not in `COMPLETE` state.

### Execution

For each instance of a pipeline (i.e. for each iteration number of a pipeline), an executor is launched by the pipeline scheduler that will periodically launch a task scheduler to perform the job of scheduling and executing tasks. After it sets up the scheduler executor service, it will wait until the pipeline is complete before returning out of the thread.

## <a name="Tasks"></a>Tasks

Tasks also have two types of threads, one that schedules tasks which in turn launches the threads that executes the task. The task scheduler thread keep track of the workflow DAG and then schedules and launches task in the proper order.

### Scheduling

Interestingly, the task scheduler has a very simple model to attempt to launch tasks: each time it is called it will attempt to launch **all** tasks in the workflow DAG. It was decided that this would be the most simple threading model for launching tasks. This means that the task scheduler will have to create a mapping of tasks to their *task dependencies in the workflow*. The task-specific dependencies are handled in the execution phase (i.e. calling [`Task.getDependencies()`](task.md#Methods)).

To schedule a task for execution it will:

1. Get the task graph from the pipeline instance
2. If the task is running or complete then it will skip scheduling the task.
3. Check if all tasks that reference the current task as "next" is complete.
4. Submit the task for execution and attach a callback listener to the thread.

Each time a pipeline is scheduled (i.e. for every pipeline interval), these steps will be taken for each task in the pipeline. Essentially the scheduler is implicitly running all tasks in the workflow at once, only deciding not to run certain tasks based on their status or the status of the previous tasks.

This means that forking in the workflow is handled very simply. Once the single task that is forking other tasks is complete, all the next tasks will be scheduled on the next execution of a pipeline.

This scheduling model was chosen over a "callback-based" or more immediate scheduling model because it was decided that simplicity is our closest friend when designing a complex workflow scheduler. Using a callback method that would launch the next tasks in workflow meant that it was highly dependent on the previous thread to be healthy and exited cleanly. We thought that this model would be more clean and simple, only depending on the persistent state storage for the scheduling of the next task.

### Execution

Once the task scheduler creates and submits the task execution thread the task is executed in the following steps:

1. Check if the task is enabled, if not then exit
2. Check if an instance of the task is already running
3. Check if the task has been already completed for this iteration number, if already completed then skip
4. Check if the task is in an error state, if true then exit
5. Initialize task by calling `init()` method
6. Check for task dependencies using the Collection of `Dependency` returned by `getDependencies()`. If all dependencies not met then exit.
7. Execute the task by calling `exec()` method
8. Update the task status (SUCCESS or ERROR)

If any of steps 1-6 fail then the task will not be executed. Debug statements are logged (if the log4j level is set to debug) for each of the steps for debugging.

### Callback

After the task execution is done, there is a callback method attached that will handle some of the post execution steps:

1. Remove the task from the list of running tasks in the task scheduler
2. Update metrics
3. If task execution threw an error, send an email to the admins

This callback method is to offload some of the non-critical, perhaps slow actions off of the main task execution thread and onto another thread.

---

[Back to Documentation](README.md)
