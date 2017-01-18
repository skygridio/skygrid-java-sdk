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
we follow [this](https://google.github.io/styleguide/javaguide.html) as our styleguide