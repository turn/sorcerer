# Sorcerer Module

[Back to Documentation](README.md)

## Configuration

The Sorcerer module has the following fields:

key  |required?|description
-----|---------|:-----------
name | yes     | name of module
pipelines |yes      |pipelines to schedule (see [Pipelines](#))
storage | yes | persistence layer (see [Storage](#))
packages | no | packages to scan for task/pipeline implementations (see [Packages](#))
email |no      |see [Email](#)


### Pipelines

This is the list of pipelines names that will be scheduled in this module. The syntax is the standard YAML list specification by prepending each entry with a `-` dash and a space.

##### Example

``` yaml
pipelines:
	- pipeline_1
	- pipeline_2
	...
```


### Storage

Sorcerer relies heavily on a persistent store to maintain the states of each task and pipeline. The type and configuration of this storage layer is specified in the module.

##### Examples
```yaml
storage:
	!hdfs
		root: /root/dir/
```
See the [Persistence Layer](persistence.md) page for more details

### Packages

Sorcere will search for `Task` and `Pipeline` implementations with the `@SorcererTask` and `@SorcererPipeline` annotations respectively (See pages [Initialization](initialization.md#Annotations), [Task](task.md#Implementation), and [Pipeline](pipeline.md#Implementation)).

By default, if no package is specified here or on runtime (see [Execution](execution.md)), Sorcerer will search through the entire java classpath which may be extremely time consuming. To optimize this you can specify the java package reference that contains your task and pipeline implementations. If this is specified, Sorcerer will recursively search through the package and find classes with the Sorcerer annotations.

##### Examples
```yaml
packages:
	- com.package.tasks
	- com.package.pipelines
```
### <a name="module-email"></a>Email

The email field is deserialized to an internal `EmailType` object so the `!` operator is required. The email object has the following fields:

key  |description
-----|:-----------
enabled |enable/disable email
admin |comma-separated list of admin emails
host |email host server

- #### enabled
  Enabled/disables sending emails from the module (disabling may be helpful for test instances)

  Options:
  - true
  - false

- #### admin

  This is the comma-separated list of admin emails that will receive emails about this module

- #### host

  In order for the email module to work properly it will need an email host server.

##### Example
```yaml
!email
	enabled: true
	admin: admin@email.com
	host: mail.server.com
```
---
[Back to Documentation](README.md)
