# FlowCommit

## How to run the project

1. create `local.gradle` file in the root directory of the project, and add the following content:

```groovy
project.ext.setProperty("localPath", "path/to/your/local/idea")
```

2. run gradle task `runIde` to start the IDE with the plugin installed.
