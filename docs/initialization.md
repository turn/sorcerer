# Sorcerer Initialization
[Back to Documentation](README.md)

Sorcerer is initialized in three steps:

1. Process configuration file
2. Process annotation
3. Reconciliation and registration

All the initialization code is executed when the `build()` method of the Sorcerer builder is called.

## <a name="Configuration"></a>Configuration files

When the Sorcerer instance is initialized, a builder is provided and one of the methods of the builder is to add configuration paths to the Sorcerer builder.

```java
Sorcerer sorcerer = Sorcerer.builder()
  .addConfigPath("/path/to/config/files")
  .build();
```
The specified path can either be a directory or a file and Sorcerer will recursively check the path and look for `*.yaml` files. *All YAML files that it finds in the path will be considered to be a sorcerer configuration file.*

The suggested configuration file structure is to have separate files for tasks, pipelines, and module and put them all in the same directory.

```
module/
  |- module.yaml
  |- pipelines.yaml
  |- tasks.yaml
```

For details on how to write configuration files see [Module](module.md), [Pipeline](pipeline.md#Configuration), and [Task](task.md#Configuration) pages.

## <a name="Annotations"></a>Annotations

After configuration files are processed, Sorcerer will search for task and pipeline Java implementations.

By default it will search the entire classpath and look for the `@SorcererTask` and `@SorcererPipeline` annotations. For details on these annotations see [Task Annotations](task.md#Annotations) and [Pipeline Annotations](pipeline.md#Annotations). Alternatively, specific Java packages can be provided to Sorcerer and it will only search in the provided packages for Sorcerer annotations. You can specify packages in two ways:

1. Specified in the `packages` field of the [module configuration](module.md#Configuration) file.
2. Specified at runtime with the `addPackage()` method

```java
Sorcerer sorcerer = Sorcerer.builder()
  .addPackage("com.package.example")
  .build();
```

## <a name="Reconciliation"></a>Reconciliation

After configuration files and the annotations have been processed, Sorcerer will eagerly try to catch any misconfigured or unimplemented tasks or pipelines by attempting to reconcile all defined objects to their corresponding classes.

First it will iterate through all classes with the `@SorcererTask` annotation and verify that it implements `Task.class`. After it has verified this it will start with the list of pipelines specified in the [module](module.md), recursively get all the tasks names in all of the pipelines, and then check to make sure that each task *and* its next tasks have a corresponding `Task.class` implementation (see [Task Annotations](task.md#Annotations)). It creates this mapping based on the task name defined in the configuration and the annotation field `name` from the `@SorcererTask` annotation. *If no `Task` implementation is found for any given task, it will log an exception and initialization will fail.*

The exception to this are tasks that specify a supported `exec` type (see [Task](task.md#Provided_task_types))

After it reconciles all tasks, it will try and reconcile pipelines. It first verifies that the classes with the `@SorcererPipeline` annotation implements `Pipeline.class`. After it does this it will try and map all pipeline definitions to its corresponding `Pipeline` implementation annotated by the `@SorcererPipeline` `name` field.

If it cannot find an implementation of `Pipeline.class` with the proper annotation it will use a default pipeline implementation which will be scheduled at every interval (see [Default Pipeline Implementation](pipeline.md#Implementation)).

## <a name="Registration"></a>Registration

Finally after all tasks and pipelines are reconciled, Sorcerer will register the module, tasks, and pipelines and create the bindings for each of them to their respective names. Sorcerer uses Google's Guice as the underlying binder and injector for it's task and pipeline registry.

---
[Back to Documentation](README.md)
