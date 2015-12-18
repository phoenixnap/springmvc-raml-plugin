![PhoenixNAP Logo](https://phoenixnap.com/wp-content/themes/phoenixnap-v2/img/v2/logo.svg)

## Spring MVC-RAML Synchronizer Parser
The Spring MVC-RAML Synchronizer project aims to provide a live representation of the endpoints exposed in a project by using the Spring MVC Annotation in that project as the single source of truth. This can be used to either generate RAML code from Spring annotations, or to keep hand-written RAML files in sync with the Spring MVC implementation by cross-checking the contract with the implementation

## Documentation & Getting Support
Usage and documentation are available in the Javadoc and README.md of the child projects. Kindly contact the developers via email (available in the pom files) if required or open an [Issue][] in our tracking system.

### Prerequisites

[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

## License
The SpringMVC-RAML plugin is released under version 2.0 of the [Apache License][].

## Usage

### Sample Code

Generating RAML from Spring MVC Annotations
```
ResourceParser scanner = new SpringMvcResourceParser(project.getBasedir().getParentFile() != null ? project.getBasedir().getParentFile() : project.getBasedir(), version, defaultMediaType, restrictOnMediaType);
RamlGenerator ramlGenerator = new RamlGenerator(scanner);
// Process the classes selected and build Raml model
ramlGenerator.generateRamlForClasses(project.getArtifactId(), version, restBasePath, classArray, this.documents);
```

Verifying RAML with Spring MVC Implementation
```
Raml published = RamlVerifier.loadRamlFromFile("test-simple.raml");
Class<?>[] classesToGenerate = new Class[] {VerifierTestController.class, SecondVerifierTestController.class, ThirdVerifierTestController.class};
Raml computed = generator.generateRamlForClasses("test", "0.0.1", "/", classesToGenerate, Collections.emptySet()).getRaml();

List<RamlChecker> checkers = new ArrayList<>();
List<RamlActionVisitorCheck> actionCheckers = new ArrayList<>();
List<RamlResourceVisitorCheck> resourceCheckers = new ArrayList<>();
List<RamlStyleChecker> styleCheckers = new ArrayList<>();
		
RamlVerifier verifier = new RamlVerifier(published, computed, checkers, actionCheckers, resourceCheckers, styleCheckers);
assertFalse("Check that there are no errors since the missing resource will get marked as a warning", verifier.hasErrors());
```

## Contributing
[Pull requests][] are welcome; Be a good citizen and create unit tests for any bugs squished or features added

[Pull requests]: http://help.github.com/send-pull-requests
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Maven]: http://maven.apache.org/
[Issue]: https://github.com/phoenixnap/springmvc-raml-plugin/issues