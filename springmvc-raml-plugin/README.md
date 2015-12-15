## Spring MVC-RAML Synchronizer Plugin
The Spring MVC-RAML Synchronizer project aims to provide a live representation of the endpoints exposed in a project by using the Spring MVC Annotation in that project as the single source of truth. This can be used to either generate RAML code from Spring annotations, or to keep hand-written RAML files in sync with the Spring MVC implementation by cross-checking the contract with the implementation

The project will extract information using reflection and source inspection (for JavaDoc) so as to expose all information available to it.

The project provides two artifacts - A maven plugin designed to be run on Java 8 code which has been compiled with argument name information. A seperate project is also supplied that allows the use of custom annotations such as @Example which can be used to embed example inputs or outputs.

## Documentation & Getting Support
Usage and documentation are available in the Javadoc and README.md (this file). Kindly contact the developers via email (available in the pom files) if required or open an [Issue][] in our tracking system.

### Prerequisites

[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

## License
The SpringMVC-To-RAML plugin  is released under version 2.0 of the [Apache License][].

## Usage

### Sample Maven Code 

The first step is to download and compile these projects using Maven. Simply run mvn clean install in the parent directory and both the plugin and annotations artifacts will be compiled. These can optionally be deployed to your
Artifactory or similar repo.

Then simply include the following code in the POM of the project you wish to generate RAML for

```
<plugin>
  <groupId>com.phoenixnap.oss</groupId>
  <artifactId>springmvc-raml-plugin</artifactId>
  <version>x.x.x</version>
  <configuration>
    <outputRamlFilePath>/src/main/resources/public/raml/api.raml</outputRamlFilePath>
    <restBasePath>/</restBasePath>
    <version>0.0.1</version>
    <restrictOnMediaType>false</restrictOnMediaType>
  </configuration>
  <executions>
    <execution>
      <id>generate-springmvc-api-docs</id>
      <phase>compile</phase>
      <goals>
        <goal>generate-springmvc-api-docs</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### outputRamlFilePath
(required) Relative file path where the RAML document will be saved to

### defaultMediaType
(optional, default: application/json) Default media Type to be used in returns/consumes where these are not specified in the code

### restBasePath
(required) Base URL relative to the generated RAML file for the APIs to be accessed at runtime

### version
(optional, default: "1") Version of the API being represented in this generation

### restrictOnMediaType
(optional, default: false) If this is set to true, we will only parse methods that consume, produce or accept the requested defaultMediaType

### documentationSuffix
(optional, default: -doc.md) The file extension that will be used to determine files that should be included as documents and linked to the generated RAML file


## Contributing
[Pull requests][] are welcome; Be a good citizen and create unit tests for any bugs squished or features added

[Pull requests]: http://help.github.com/send-pull-requests
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Maven]: http://maven.apache.org/
[Issue]: https://github.com/phoenixnap/springmvc-raml-plugin/issues