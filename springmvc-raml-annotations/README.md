## Spring MVC-to-RAML Generator
The Spring MVC-to-RAML Generator annotations project allows the use of custom annotations such as @Example which can be used to embed example inputs or outputs.

## Documentation
See the current [Javadoc][] and [reference docs][].

## Getting Support
Usage and documentation are available in the Javadoc and README.md of the childe projects. Kindly contact the developers via email (available in the pom files) if required or open an issue in our tracking system.

## Building from Source
The SpringMVC-To-RAML plugin uses a [Maven][]-based build system.

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