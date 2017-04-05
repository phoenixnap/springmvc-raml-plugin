## Spring MVC-RAML Synchronizer Plugin
The Spring MVC-RAML Synchronizer project aims to provide a live representation of the endpoints exposed in a project by using the Spring MVC Annotation in that project as the single source of truth. This can be used to either generate RAML code from Spring annotations, or to keep hand-written RAML files in sync with the Spring MVC implementation by cross-checking the contract with the implementation. In the third usage mode you can generate Spring MVC server endpoints (RestControllers) from a provided RAML document.

The project will extract information using reflection and source inspection (for JavaDoc) so as to expose all information available to it.

The project provides two artifacts - A maven plugin designed to be run on Java 8 code which has been compiled with argument name information. A seperate project is also supplied that allows the use of custom annotations such as @Example which can be used to embed example inputs or outputs.
![PhoenixNAP Logo](https://phoenixnap.com/wp-content/themes/phoenixnap-v2/img/v2/logo.svg)

## Documentation & Getting Support
Usage and documentation are available in the Javadoc and README.md (this file). Kindly contact the developers via email (available in the pom files) if required or open an [Issue][] in our tracking system.

### Prerequisites

[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

## License
The SpringMVC-To-RAML plugin  is released under version 2.0 of the [Apache License][].

## Usage 1 - Generating RAML from Implementation

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
	<javaDocPath>D:/</javaDocPath>
    <restBasePath>/</restBasePath>
    <version>0.0.1</version>
    <includeGlobalMediaType>false</includeGlobalMediaType>
	<restrictOnMediaType>false</restrictOnMediaType>
	<ignoredList>
	   <param>com.package.to.ignore</param>
	   <param>com.specificClass.to.ignore.ClassName</param>
	</ignoredList>
	<dependencyPackagesList>
	   <param>com.package.in.dependency.jar.to.include</param>
	</dependencyPackagesList>
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

### createPathIfMissing
(optional, default: false) If this is set to true, we will create the path directories if they don't exist

### removeOldOutput
(optional, default: false) If this is set to true, we will empty the output directory before generation occurs

### includeGlobalMediaType
(optional, default: false) If this is set to true, we will append the default media type to the global RAML mediaType property

### javaDocPath
(optional) Absolute path to a folder which will be used to search for JavaDoc. This folder should contain all source of controllers being scanned. Using root folders for this may increase scanning time. If this is not supplied, the scanner will default to the current project folder

### defaultMediaType
(optional, default: application/json) Default media Type to be used in returns/consumes where these are not specified in the code

### restBasePath
(required) Base URL relative to the generated RAML file for the APIs to be accessed at runtime

### version
(optional, default: "1") Version of the API being represented in this generation

### restrictOnMediaType
(optional, default: false) If this is set to true, we will only parse methods that consume, produce or accept the requested defaultMediaType

### ignoredList
(optional, default: empty) If classes or packages are included in this list, they will not be included in the generated model

### dependencyPackagesList
(optional, default: empty) If packages are included in this list, they will be also added to the packages that will be scanned

### documentationSuffix
(optional, default: -doc.md) The file extension that will be used to determine files that should be included as documents and linked to the generated RAML file

## Usage 2 - Style Checking RAML document and Verifying against Implementation

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
    <ramlToVerifyPath>/src/main/resources/public/raml/api.raml</ramlToVerifyPath>
	<javaDocPath>D:/</javaDocPath>
    <performStyleChecks>true</performStyleChecks>
	<checkRamlAgainstImplementation>true</checkRamlAgainstImplementation>
	<checkForResourceExistence>true</checkForResourceExistence>
	<checkForActionExistence>true</checkForActionExistence>
	<checkForQueryParameterExistence>true</checkForQueryParameterExistence>
	<checkForPluralisedResourceNames>true</checkForPluralisedResourceNames>
	<checkForSpecialCharactersInResourceNames>true</checkForSpecialCharactersInResourceNames>
	<checkForDefinitionOf40xResponseInSecuredResource>true</checkForDefinitionOf40xResponseInSecuredResource>
	<checkForSchemaInSuccessfulResponseBody>true</checkForSchemaInSuccessfulResponseBody>
	<checkForSchemaInRequestBody>true</checkForSchemaInRequestBody>
	<checkForDefinitionOf404ResponseInGetRequest>true</checkForDefinitionOf404ResponseInGetRequest>
	<checkForResponseBodySchema>true</checkForResponseBodySchema>
	<breakBuildOnWarnings>false</breakBuildOnWarnings>
    <logWarnings>true</logWarnings>
    <logErrors>true</logErrors>
	<ignoredList>
	   <param>com.package.to.ignore</param>
	   <param>com.specificClass.to.ignore.ClassName</param>
	</ignoredList>
	<dependencyPackagesList>
	   <param>com.package.in.dependency.jar.to.include</param>
	</dependencyPackagesList>
  </configuration>
  <executions>
    <execution>
      <id>generate-springmvc-api-docs</id>
      <phase>compile</phase>
      <goals>
        <goal>verify-springmvc-api-docs</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### ramlToVerifyPath
(required) Relative file path where the RAML document to verify will be loaded from.

### javaDocPath
(optional) Absolute path to a folder which will be used to search for JavaDoc. This folder should contain all source of controllers being scanned. Using root folders for this may increase scanning time. If this is not supplied, the scanner will default to the current project folder

### uriPrefixToIgnore
(optional) A Portion of the URL of the implemented RAML that will be ignored when verifying

### checkRamlAgainstImplementation
(optional, default: true) Flag that will enable or disable checking of the RAML file against the Server Implementation. If this is set to false it will override other check flags

### performStyleChecks
(optional, default: true) Flag that will enable or disable Style Checking of the RAML file. If this is set to false it will override other style check flags

### checkForResourceExistence
(optional, default: true) Flag that will enable or disable Checks for existence of Resources

### checkForActionExistence
(optional, default: true) Flag that will enable or disable Checks for existence of Actions/Verbs

### checkForActionContentType
(optional, default: true) Flag that will enable or disable Checks for content type in request/response of Actions/Verbs

### checkForQueryParameterExistence
(optional, default: true) Flag that will enable or disable checks for existence of query parameters

### checkForPluralisedResourceNames
(optional, default: true) Flag that will enable or disable checks for plural resources names

### checkForSpecialCharactersInResourceNames
(optional, default: true) Flag that will enable or disable checks for special characters in URLs

### checkForDefinitionOf40xResponseInSecuredResource
(optional, default: true) Flag that will enable or disable checks for 401 and 403 responses for secured resources

### checkForSchemaInSuccessfulResponseBody
(optional, default: "", recommended: "GET, PUT, POST") Comma separated list of HTTP Methods to check for schemas being present in the successful response Body.

### checkForSchemaInRequestBody
(optional, default: "", recommended: "PUT, POST") Comma separated list of HTTP Methods to check for schemas being present in the request Body.

### checkForDefinitionOf404ResponseInGetRequest
(optional, default: false) Flag that will enable or disable checks for 404 responses get requests

### checkForDefinitionOfErrorCodes
(optional, default: false) Flag that will enable or disable checks for 400 and 500 error codes in certain verbs (PUT, POST, PATCH for 400 & All verbs for 500)

### checkForResponseBodySchema
(optional, default: false) Flag that will enable or disable response body schema validation against implementation

### ignoredList
(optional, default: empty) If classes or packages are included in this list, they will not be included in the generated model

### dependencyPackagesList
(optional, default: empty) If packages are included in this list, they will be also added to the packages that will be scanned

### breakBuildOnWarnings
(optional, default: false) Flag that will enable or disable braking of the build if Warnings are found

### logWarnings
(optional, default: false) Flag that will enable or disable logging of warning level issues to standard out if found

### logErrors
(optional, default: false) Flag that will enable or disable logging of error level Issues to standard out if found

## Usage 3 - Generating SpringMVC Server Endpoints from a RAML file

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
    <ramlPath>{path.to.raml.file}</ramlPath>
	<schemaLocation>{path.to.schema.directory||schema.absolute.url}</schemaLocation>
	<outputRelativePath>/src/generated</outputRelativePath>
    <addTimestampFolder>false</addTimestampFolder>
    <basePackage>com.gen.wow</basePackage>
    <baseUri>/api</baseUri>
    <generateUnreferencedSchemas>true</generateUnreferencedSchemas>
	<generationConfig>
		<includeAdditionalProperties>false</includeAdditionalProperties>
		...
	</generationConfig>
	<seperateMethodsByContentType>false</seperateMethodsByContentType>
	<rule>com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerStubRule</rule>
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
(optional) Relative path where the generated Java classes will be saved to. Package structure folders will be created relative to this path.

### addTimestampFolder
(optional, default: false) Should an extra folder be generated using a timestamp to seperate generations

### basePackage
(required) Base package to be used for the java classes to be generated. Model objects will be added in the .model subpackage

### schemaLocation
(optional, default: "") The URI or relative path to the folder/network location containing JSON Schemas

### baseUri
(optional) Base URI for generated Spring controllers. This overrules the baseUri attribute from inside the .raml spec.

### generateUnreferencedSchemas
(optional) Determines whether POJOs for unreferenced schemas included in the RAML file should be generated.

### generationConfig
(optional) This object contains a map of configuration for the JsonSchema2Pojo generator. The full list of configurable attributes, their description and default values can be found here [GenerationConfig][]

### injectHttpHeadersParameter
(optional, default: false) If set to true, we will generate a `HttpHeaders` parameter for each method to allow using request HTTP headers directly.

### seperateMethodsByContentType
(optional, default: false) Should we generate separate API methods for endpoints which define multiple content types in their 200 response.

### useJackson1xCompatibility
(optional, default: false) If set to true, we will generate Jackson 1 annotations inside the model objects.

### resourceDepthInClassNames
(optional, default: 1) Levels of resource path that will be included in generated class names. If set to -1 entire uri will be included in class name.

### ruleConfiguration
(optional) This is a key/value map for configuration of individual rules. Not all rules support configuration.

### rule
(optional, default: com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerStubRule) The rule class to be used for code generation.

#### Available Rules
- **com.phoenixnap.oss.ramlapisync.generation.rules.Spring3ControllerStubRule**:
- **com.phoenixnap.oss.ramlapisync.generation.rule.Spring4ControllerStubRule**:
The standard rule. It creates simple controller stubs classes with Spring MVC annotations and empty method bodies (like in v.0.2.4).
All you have to do is to implement the empty method body for each endpoint. This is simple and easy.
The drawback: When you regenerate the controller stubs your code will be overriden.

```
Configuration:
	callableResponse: [OPTIONAL] set to 'true' to support asynchronous callables. Default: 'false'
```

- **com.phoenixnap.oss.ramlapisync.generation.rules.Spring3ControllerDecoratorRule**:
- **com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerDecoratorRule**:
Creates a controller interface and a decorator with Spring MVC annotations for each top level endpoint.
The decorator implements the controller interface and delegates all method calls to an @Autowired ControllerDelegate.
So all you have to do is to provide an ControllerDelegate class which implements the controller interface.

```
Configuration:
	callableResponse: [OPTIONAL] set to 'true' to support asynchronous callables. Default: 'false'
```

- **com.phoenixnap.oss.ramlapisync.generation.rules.Spring3ControllerInterfaceRule**:
- **com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerInterfaceRule**:
Creates an single interface with Spring MVC annotations for each top level endpoint.
All you have to do is to provide an implementation for the controller interface

```
Configuration:
	callableResponse: [OPTIONAL] set to 'true' to support asynchronous callables. Default: 'false'
```

- **com.phoenixnap.oss.ramlapisync.generation.rules.Spring4RestTemplateClientRule**:
Creates a single interface as well as a client implementation using the Spring RestTemplate. The client assumes that a RestTemplate is available to be autowired.

```
Configuration:
	baseUrlConfigurationPath: The path that will be used to load the property for the server url. Default: ${client.url}
	restTemplateFieldName: The name of the RestTemplate field
	restTemplateQualifierBeanName: [OPTIONAL] The name of the bean for the rest template used in the generated client. Default: NONE
```

- **com.phoenixnap.oss.ramlapisync.generation.rules.SpringFeignClientInterfaceRule**:
Creates a standalone `org.springframework.cloud.netflix.feign.FeignClient` (REST client) for each top level endpoint.

## Contributing
[Pull requests][] are welcome; Be a good citizen and create unit tests for any bugs squished or features added

[GenerationConfig]: https://github.com/phoenixnap/springmvc-raml-plugin/blob/master/springmvc-raml-plugin/src/main/java/com/phoenixnap/oss/ramlapisync/plugin/PojoGenerationConfig.java
[Pull requests]: http://help.github.com/send-pull-requests
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Maven]: http://maven.apache.org/
[Issue]: https://github.com/phoenixnap/springmvc-raml-plugin/issues
