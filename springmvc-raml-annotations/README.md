![PhoenixNAP Logo](https://phoenixnap.com/wp-content/themes/phoenixnap-v2/img/v2/logo.svg)

## Spring MVC-RAML Synchroniser
The Spring MVC-RAML Synchronizer annotations project allows the use of custom annotations such as @Example which can be used to embed example inputs or outputs.

## Documentation & Getting Support
Usage and documentation are available in the Javadoc and README.md of the child projects. Kindly contact the developers via email (available in the pom files) if required or open an [Issue][] in our tracking system.

## Building from Source
The SpringMVC-RAML plugin uses a [Maven][]-based build system.

## Prerequisites
[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

## Usage

```
	@Description(pathDescriptions = { @PathDescription(key="order", value="Order operations managed by OrderMonkey"),
									  @PathDescription(key="{configName}", value="Order Name is Nameish")})
	@RequestMapping(value="/order/{orderName}/stuff", method= RequestMethod.GET)
	public Stuff getOrderNamedStuff(...)
```

```
	@RequestMapping(value="/order/{orderName}/stuff", method= RequestMethod.GET)
	public Stuff getOrderNamedStuff(@PathVariable @Example("supaOrder") String orderName)
```

## License
The SpringMVC-To-RAML plugin  is released under version 2.0 of the [Apache License][].

## Contributing
[Pull requests][] are welcome; Be a good citizen and create unit tests for any bugs squished or features added

[Pull requests]: http://help.github.com/send-pull-requests
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Maven]: http://maven.apache.org/
[Issue]: https://github.com/phoenixnap/springmvc-raml-plugin/issues