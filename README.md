# The java SDK for the skygrid platform

## Requirements
* jdk8 (we're using lambdas)
* gradle

## Gradle tasks
* `gradle mainJar`
    * includes all dependencies
    * includes the main class (this will be removed later)
    * run `java -jar build/libs/main.jar` to run the main class
    
## Contributing
* we follow [this](https://google.github.io/styleguide/javaguide.html) as our styleguide
* google's GSON is used to handle JSON objects and arrays
* JSON objects are used internally but the end user is not expected to use GSON hence all public methods and classes must not return JSON Objects neither should it have as arguments 