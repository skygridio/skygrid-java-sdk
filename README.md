# The java SDK for the skygrid platform

## Requirements
* jdk8 (we're using lambdas)
* gradle

## Gradle tasks
* `gradle mainJar`
    * creates a *main.jar*
    * includes all dependencies
    * includes the main class
    * run `java -jar build/libs/main.jar` to run the main class

* `gradle pkgJar`
    * creates *pkg.jar*
    * includes skygrid components and Gson
    * is not runnable
    * include this jar in your projects

* `gradle docs`
    * creates the javadocs
    * found in build/docs

* `gradle release`
    * TODO
    * creates a github release?
    * pushes new package to maven Central

## Contributing
* we follow [google's](https://google.github.io/styleguide/javaguide.html) styleguide
* google's GSON is used to handle JSON objects and arrays
* JSON objects are used internally but the end user is not expected to use GSON hence all public methods and classes must not return JSON Objects neither should it have as arguments (as fas as possible)
