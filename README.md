## Spring MVC-RAML Synchronizer
The Spring MVC-RAML Sychronizer parent project aims to provide a live representation of the endpoints exposed in a project by using the Spring MVC Annotation in that project as the single source of truth. This can be used to either generate RAML code from Spring annotations, or to keep hand-written RAML files in sync with the Spring MVC implementation by cross-checking the contract with the implementation

When generating RaML, the project will extract information using reflection and source inspection (for JavaDoc) so as to expose all information available to it.

The project provides three artifacts:
- springmvc-raml-plugin: A maven plugin designed to be run on Java 8 code which has been compiled with argument name information. 
- springmvc-raml-parser: This is a seperate project that contains the parser which converts Spring MVC annotations to a RAML Model
- springmvc-raml-annotations: This project allows the use of custom annotations such as @Example which can be used to embed example inputs or outputs.

## Documentation
See the current [Javadoc][] and [reference docs][].

## Getting Support
Usage and documentation are available in the Javadoc and README.md of the childe projects. Kindly contact the developers via email (available in the pom files) if required or open an issue in our tracking system.

## Building from Source
The SpringMVC-RAML plugin uses a [Maven][]-based build system.

## Prerequisites
[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

## License
The SpringMVC-To-RAML plugin  is released under version 2.0 of the [Apache License][].