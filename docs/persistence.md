<!---
  Copyright (c) 2015, Turn Inc. All Rights Reserved.
  Use of this source code is governed by a BSD-style license that can be found
  in the LICENSE file.
-->

# Sorcerer Persistence Layer

[Back to Documentation](README.md)

Sorcerer relies on a persistent storage layer to store pipeline and task states.
Objects can be in the following states:

- PENDING
- RUNNING
- SUCCESS
- ERROR

Depending on the storage layer implementation, these states can have different forms of representation.

### Types

Sorcerer comes with some storage layer implementations:

- ### HDFS

  Sorcerer does not have advanced HDFS setup parameters. Your hadoop configuration should be specified in the classpath.

  When the HDFS storage layer is initialized, it uses the user-specified root directory as defined in the configuration (see [Module Storage](module.md#Storage)).

  ```YAML
  ...
    !hdfs
      root: /status/root/dir/
  ```

  The first step in the HDFS storage layer initialization is testing the HDFS connection by checking if the `task/` and `pipeline/` directories are created. If the root directory or either of the object directories do not exist, Sorcerer will create the directories. This is done in Sorcerer initialization step (see [Initialization](initialization.md) page).

  ```
  /status/root/dir/tasks/
  /status/root/dir/pipelines/
  ```

  After initialization Sorcerer will keep track of pipeline and task states per iteration by putting an empty file under a directories named by the iteration number in the corresponding object directory with file names equal to their respective state name [RUNNING, SUCCESS, ERROR].

  ```
  # Success status
  /status/root/dir/tasks/{iterationNumber}/SUCCESS

  # In progress status
  /status/root/dir/tasks/{iterationNumber}/RUNNING

  # Error status
  /status/root/dir/tasks/{iterationNumber}/ERROR
  ```

  The PENDING state is represented by the **absence** of the iteration number in the object directory.

- ### MySQL

  Coming Soon!

- ### Zookeeper

  Coming Soon!

---
[Back to Documentation](README.md)
