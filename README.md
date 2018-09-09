![PhoenixNAP Logo](https://phoenixnap.com/wp-content/themes/phoenixnap-v2/img/v2/logo.svg)

# Spring MVC-RAML Plugin [![Build Status](https://travis-ci.org/phoenixnap/springmvc-raml-plugin.svg?branch=master)](https://travis-ci.org/phoenixnap/springmvc-raml-plugin) [![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.phoenixnap.oss/springmvc-raml-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.phoenixnap.oss/springmvc-raml-plugin/badge.svg)

The Spring MVC-RAML project aims to enforce contract-first approach for projects using Spring MVC framework. The idea is to manually maintain RAML file as a single source of truth and to use this plugin to generate web layer - Spring controllers and domain objects. If the plugin is used as part of a build, application's code will always be in line with RAML documentation.


**The plugin is designed to be run on Java 8 code which has been compiled with argument name information.**


## Sample Project
A sample project that includes a SpringMVC Server implementation using the Decorator pattern as well as a RestTemplate based REST client is available here:  [SpringMVC RAML Contract First Sample]( https://github.com/phoenixnap/springmvc-raml-plugin-sample/). This sample is based on the contract first scenario whereby the RAML document is authored and used as the basis for implementation


## Documentation & Getting Support
Usage and documentation are available in the Javadoc and README.md (this file). Kindly contact the developers via email (available in the pom files) if required or open an [Issue][] in our tracking system.


## Building from Source
The SpringMVC-RAML plugin uses a [Maven][]-based build system.


## Prerequisites
[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.


## License
The SpringMVC-RAML plugin is released under version 2.0 of the [Apache License][].


## Contributing
[Pull requests][] are welcome; Be a good citizen and create unit tests for any bugs squished or features added


## Usage - Generating SpringMVC Server Endpoints from a RAML file

### Sample Maven Code

The first step is to download and compile these projects using Maven. Simply run `mvn clean install` in the parent directory and artifact will be compiled. These can optionally be deployed to your Artifactory or similar repo.


Then simply include the following code in the POM of the project you wish to generate RAML for

```
<plugin>
  <groupId>com.phoenixnap.oss</groupId>
  <artifactId>springmvc-raml-plugin</artifactId>
  <version>2.x.x</version>
  <configuration>
    <ramlPath>{path.to.raml.file}</ramlPath>
    <schemaLocation>{path.to.schema.directory||schema.absolute.url}</schemaLocation>
    <outputRelativePath>/src/generated</outputRelativePath>
    <addTimestampFolder>false</addTimestampFolder>
    <basePackage>com.gen.wow</basePackage>
    <baseUri>/api</baseUri>
    <generateUnreferencedObjects>true</generateUnreferencedObjects>
    <generationConfig>
        <includeAdditionalProperties>false</includeAdditionalProperties>
        ...
    </generationConfig>
    <seperateMethodsByContentType>false</seperateMethodsByContentType>
    <rule>com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule</rule>
    <ruleConfiguration>			
    </ruleConfiguration>
  </configuration>
  <executions>
    <execution>
      <id>generate-springmvc-endpoints</id>
      <phase>compile</phase>
      <goals>
        <goal>generate-springmvc-endpoints</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### ramlPath
(required) The path to the file, relative to the project base directory

### outputRelativePath
(optional, default: "") Relative path where the generated Java classes will be saved to. Package structure folders will be created relative to this path.

### addTimestampFolder
(optional, default: `false`) Should an extra folder be generated using a timestamp to seperate generations

### basePackage
(required) Base package to be used for the java classes to be generated. Model objects will be added in the .model subpackage

### schemaLocation
(optional, default: "") The URI or relative path to the folder/network location containing JSON Schemas

### baseUri
(optional, default: "") Base URI for generated Spring controllers. This overrules the baseUri attribute from inside the .raml spec.

### generateUnreferencedObjects
(optional, default: `false`) Determines whether POJOs for unreferenced schemas or data types included in the RAML file should be generated.

### generationConfig
(optional) This object contains a map of configuration for the JsonSchema2Pojo generator. The full list of configurable attributes, their description and default values can be found here [GenerationConfig][]

### injectHttpHeadersParameter
(optional, default: `false`) If set to true, we will generate a `HttpHeaders` parameter for each method to allow using request HTTP headers directly.

### seperateMethodsByContentType
(optional, default: `false`) Should we generate separate API methods for endpoints which define multiple content types in their 200 response.

### useJackson1xCompatibility
(optional, default: `false`) If set to true, we will generate Jackson 1 annotations inside the model objects.

### resourceDepthInClassNames
(optional, default: `1`) Levels of resource path that will be included in generated class names. If set to -1 entire uri will be included in class name.

### resourceTopLevelInClassNames
(optional, default: `0`) Top level of resource path that will be included in generated class names. If set to 0 entire URI will be included in class name.

### reverseOrderInClassNames
(optional, default: `false`) Reverse order of resource path that will be included in generated class names. If set to false URI will be included in class name from left to right.

### methodsNamingLogic
(optional, default: `OBJECTS`) Logic used for Java methods name generation. Possible values: `OBJECTS` (objects like request parameters and return types will be used) and `RESOURCES` (resource path will be used).

NOTE: This is different from a previous default. Use `RESOURCES` to get `0.x` behavior.

### overrideNamingLogicWith
(optional, default: "") The way to override naming logic for Java methods and arguments. Possible values:
 - `DISPLAY_NAME` (`displayName` attribute (if found) will be cleaned and used. `displayName` key is natively supported by RAML spec)
 - `ANNOTATION` (`javaName` annotation (if found) will be used as is). Refer to RAML [Annotation](https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md#annotations) for more details.
 
### dontGenerateForAnnotation
(optional, default: "") When defined, code generation will be skipped for resources and methods annotated with this [Annotation](https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md#annotations). When annotation is set on resource - all methods in the resource and all sub-resources will be ignored. Value of the annotation is not important.

### ruleConfiguration
(optional) This is a key/value map for configuration of individual rules. Not all rules support configuration.

### rule
(optional, default: `com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule`) The rule class to be used for code generation.

#### Available Rules
- **com.phoenixnap.oss.ramlapisync.generation.rule.Spring4ControllerStubRule**:
The standard rule. It creates simple controller stubs classes with Spring MVC annotations and empty method bodies (like in v.0.2.4).
All you have to do is to implement the empty method body for each endpoint. This is simple and easy.
The drawback: When you regenerate the controller stubs your code will be overriden.

```
Configuration:
	callableResponse: [OPTIONAL] set to 'true' to support asynchronous callables. Default: 'false'
```

- **com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerDecoratorRule**:
Creates a controller interface and a decorator with Spring MVC annotations for each top level endpoint.
The decorator implements the controller interface and delegates all method calls to an @Autowired ControllerDelegate.
So all you have to do is to provide an ControllerDelegate class which implements the controller interface.

```
Configuration:
	callableResponse: [OPTIONAL] set to 'true' to support asynchronous callables. Default: 'false'
```

- **com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerInterfaceRule**:
Creates an single interface with Spring MVC annotations for each top level endpoint.
All you have to do is to provide an implementation for the controller interface

```
Configuration:
	callableResponse: [OPTIONAL] set to 'true' to support asynchronous callables. Default: 'false'
	simpleReturnTypes: [OPTIONAL] set to 'true' to generate controllers method's return types without ResponseEntity<> wrapper. Will also generate Object instead of ResponseEntity<?> return type for methods when return type is not specified for the endpoint. Default: 'false'
	useShortcutMethodMappings: [OPTIONAL] set to 'true' to generate new shortcut method annotations(e.g. @PutMapping, @GetMapping) instead of old-style @RequestMapping. Default: 'false'
```

- **com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4RestTemplateClientRule**:
Creates a single interface as well as a client implementation using the Spring RestTemplate. The client assumes that a RestTemplate is available to be autowired.

```
Configuration:
	baseUrlConfigurationPath: The path that will be used to load the property for the server url. Default: ${client.url}
	restTemplateFieldName: The name of the RestTemplate field
	restTemplateQualifierBeanName: [OPTIONAL] The name of the bean for the rest template used in the generated client. Default: NONE
```

- **com.phoenixnap.oss.ramlplugin.raml2code.rules.SpringFeignClientInterfaceRule**:
Creates a standalone `org.springframework.cloud.netflix.feign.FeignClient` (REST client) for each top level endpoint.


[Pull requests]: http://help.github.com/send-pull-requests
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Maven]: http://maven.apache.org/
[Issue]: https://github.com/phoenixnap/springmvc-raml-plugin/issues
[GenerationConfig]: https://github.com/phoenixnap/springmvc-raml-plugin/blob/v20/src/main/java/com/phoenixnap/oss/ramlplugin/raml2code/plugin/PojoGenerationConfig.java
