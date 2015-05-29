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

For each instance of a pipeline (i.e. for each iteration number of a pipeline), a thread is launched by the pipeline scheduler that will create a task scheduler to perform the job of scheduling and executing tasks. The pipeline execution thread however is there to basically monitor

## <a name="Tasks"></a>Tasks

### Scheduling

### Execution

## <a name="Status"></a>Status

---

[Back to Documentation](README.md)
