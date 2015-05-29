# Sorcerer Pipeline
[Back to Documentation](README.md)

## <a name="Configuration"></a>Configuration

key|required?|description
-|-|:-
name|yes|pipeline name unique in the pipeline namespace
init|yes|first task in the pipeline
interval|no|interval in seconds between attempted runs
lookback|no|# of previous pipeline iterations to schedule
threads|no|number of max threads allowed for the pipeline
cron|no|cron string

##### Examples
```
---
!pipeline
  name: new_pipeline

  # Initial task name
  init: new_task

  # Interval in seconds between pipeline attempts
  interval: 120

  # Previous iterations to attempt scheduling
  lookback: 1

  # Max number of threads
  threads: 5

  # Cron
  cron: * 5 * * * *

---
...
```

## <a name="Implementation"></a>Implementation

After a pipeline configuration is defined, the user has the option to implement a trigger to launch the pipeline. For example if a pipeline should only be launched when a path is created in HDFS then an implementation of `Pipeline` should be created (See [getCurrentIterationNumber()](#getCurrentIterationNumber)).

## <a name="Annotation">Annotation

First, the implementation of `Pipeline` should be annotated with the `@SorcererPipeline` annotation with the `name` field populated. This name string will be mapped to the corresponding pipeline name from the pipeline configuration in the configuration files.

```java
@SorcererPipeline(name = "pipeline_name")
```

## <a name="Method">Methods

Along with the annotation, The `Pipeline` interface has the following methods to be implemented:

- #### getCurrentIterationNumber()
  This method is used to generate a new iteration number for the pipeline. Keep in mind that Sorcerer attempts to schedule a pipeline based on its iteration number. **Whenever a new iteration number is generated, a new pipeline instance will be scheduled.** This can be used by the user to define when to trigger a new iteration of the pipeline.

  A good example of this would be if a pipeline should be scheduled whenever a new file exists in a HDFS directory. The implemented method would then list the files statuses in an HDFS directory, find the latest file, and then generate an iteration number based on the last modified timestamp of the file. This means that it will keep generating the same iteration number as long as the latest file is the same, but when a new file is created in the directory a new iteration number will be generated.

  ```java
  @Override
  public int getCurrentIterationNumber() {
    Filesystem fs = FileSystem.get(new Configuration());

    FileStatus[] statuses = dfs.globStatus("/input/file/dir/");

    return getMaxmiumTimestamp(statuses);
  }
  ```

Note that if the `cron` field in the pipeline configuration is populated then Sorcerer will automatically bind the pipeline type to an instance of [Cron Pipeline](#Cron_pipeline) and ignores any implementation of `Pipeline` even if the annotation name matches.

#### <a name="Default_pipeline">Default pipeline

If no `Pipeline` implementation is found, Sorcerer will use a default pipeline for the pipeline type. The default pipeline will generate a new iteration number for every attempted run of the pipeline. Basically it will return the current iteration number of the pipeline type incremented by one.


```java
@Override
public Integer getCurrentIterationNumber() {
  return StatusManager.get().getCurrentIterationNumberForPipeline(type) + 1;
}
```

#### <a name="Cron_pipeline">Cron pipeline

If the `cron` field in the pipeline configuration is specified, then Sorcerer will automatically bind the pipeline to an instance of `CronPipeline` which will generate current iteration number based on the date and how many iterations of the pipeline in a day.

---
[Back to Documentation](README.md)
