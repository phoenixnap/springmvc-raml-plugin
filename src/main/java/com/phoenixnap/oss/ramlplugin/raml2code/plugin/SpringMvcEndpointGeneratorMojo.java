/*
 * Copyright 2002-2017 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.Jackson1Annotator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlParser;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10.RJP10V2RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.ConfigurableRule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.RamlLoader;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * Maven Plugin MOJO specific to Generation of Spring MVC Endpoints from RAML
 * documents.
 *
 * @author Kurt Paris
 * @since 0.2.1
 */
@Mojo(name = "generate-springmvc-endpoints", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true, requiresProject = false)
public class SpringMvcEndpointGeneratorMojo extends AbstractMojo {

	/**
	 * Maven project - required for project info
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;

	@Parameter(defaultValue = "${plugin}", readonly = true)
	private PluginDescriptor descriptor;

	/**
	 * Path to the raml document to be verified
	 */
	@Parameter(property = "ramlPath", required = true, readonly = true, defaultValue = "")
	protected String ramlPath;

	/**
	 * Path to the pom document to be verified
	 */
	@Parameter(property = "pomPath", required = false, readonly = true, defaultValue = "NA")
	protected String pomPath;

	/**
	 * Relative file path where the Java files will be saved to
	 */
	@Parameter(property = "outputRelativePath", required = false, readonly = true, defaultValue = "")
	protected String outputRelativePath;

	/**
	 * IF this is set to true, we will only parse methods that consume, produce
	 * or accept the requested defaultMediaType
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean addTimestampFolder;

	/**
	 * Java package to be applied to the generated files
	 */
	@Parameter(property = "basePackage", required = true, readonly = true, defaultValue = "")
	protected String basePackage;

	/**
	 * The URI or relative path to the folder/network location containing JSON
	 * Schemas
	 */
	@Parameter(required = false, readonly = true, defaultValue = "")
	protected String schemaLocation;

	/**
	 * A boolean indicating whether the POJOs for unreferenced schemas defined
	 * in the RAML file should be generated. By default, such schemas are not
	 * generated.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean generateUnreferencedSchemas;

	/**
	 * The explicit base path under which the rest endpoints should be located.
	 * If overrules the baseUri setting in the raml spec.
	 */
	@Parameter(required = false, readonly = true)
	protected String baseUri;

	/**
	 * If set to true, we will generate seperate methods for different content
	 * types in the RAML
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean seperateMethodsByContentType;

	/**
	 * If set to true, we will generate Jackson 1 annotations inside the model
	 * objects
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean useJackson1xCompatibility;

	/**
	 * The full qualified name of the Rule that should be used for code
	 * generation.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule")
	protected String rule;

	/**
	 * Map of key/value configuration parameters that can be used to modify
	 * behaviour or certain rules
	 */
	@Parameter(required = false, readonly = true)
	protected Map<String, String> ruleConfiguration = new LinkedHashMap<>();

	/**
	 * Configuration passed to JSONSchema2Pojo for generation of pojos.
	 */
	@Parameter(required = false, readonly = true)
	protected PojoGenerationConfig generationConfig = new PojoGenerationConfig();

	/**
	 * If set to true, we will generate methods with HttpHeaders as a parameter
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean injectHttpHeadersParameter;

	/**
	 * How many levels of uri will be included in generated class names. Default
	 * is 1 which means that only current resource will be in included in
	 * controller/decorator names.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "1")
	protected Integer resourceDepthInClassNames;

	/**
	 * Top level of URI included in generated class names. Default is 0 which
	 * means that all resources will be in included in controller/decorator
	 * names.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "0")
	protected Integer resourceTopLevelInClassNames;

	/**
	 * Reverse order of resource path that will be included in generated class
	 * names. Default is false which means that resources will be in included in
	 * controller/decorator names from left to right.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean reverseOrderInClassNames;

	private ClassRealm classRealm;

	private String resolvedSchemaLocation;

	protected void generateEndpoints() throws IOException {

		File pomFile = null;
		if (!pomPath.equals("NA")) {

			Model model = null;
			FileReader reader = null;
			MavenXpp3Reader mavenreader = new MavenXpp3Reader();
			pomFile = new File(pomPath);
			try {
				reader = new FileReader(pomFile);
				model = mavenreader.read(reader);
				model.setPomFile(pomFile);
			} catch (Exception ex) {
				getLog().info("Exception Occured", ex);
			}
			project = new MavenProject(model);
			project.setFile(pomFile);
		}

		String resolvedPath = project.getBasedir().getAbsolutePath();
		if (resolvedPath.endsWith(File.separator) || resolvedPath.endsWith("/")) {
			resolvedPath = resolvedPath.substring(0, resolvedPath.length() - 1);
		}

		String resolvedRamlPath = project.getBasedir().getAbsolutePath();

		if (!ramlPath.startsWith(File.separator) && !ramlPath.startsWith("/")) {
			resolvedRamlPath += File.separator + ramlPath;
		} else {
			resolvedRamlPath += ramlPath;
		}

		// Resolve schema location and add to classpath
		resolvedSchemaLocation = getSchemaLocation();

		RamlRoot loadRamlFromFile = RamlLoader.loadRamlFromFile(new File(resolvedRamlPath).toURI().toString());

		JCodeModel codeModel = null;
		// In the RJP10V2 we have support for a unified code model. RJP08V1 does
		// not work well with this.
		boolean unifiedModel = false;
		if (loadRamlFromFile instanceof RJP10V2RamlRoot) {
			codeModel = new JCodeModel();
			unifiedModel = true;
		}

		// init configuration
		Config.setMojo(this);

		RamlParser par = new RamlParser(getBasePath(loadRamlFromFile));
		Set<ApiResourceMetadata> controllers = par.extractControllers(codeModel, loadRamlFromFile);

		if (StringUtils.hasText(outputRelativePath)) {
			if (!outputRelativePath.startsWith(File.separator) && !outputRelativePath.startsWith("/")) {
				resolvedPath += File.separator;
			}
			resolvedPath += outputRelativePath;
		} else {
			resolvedPath += "/target/generated-sources/spring-mvc";
		}

		File rootDir = new File(resolvedPath + (addTimestampFolder == true ? System.currentTimeMillis() : "") + "/");

		if (!rootDir.exists() && !rootDir.mkdirs()) {
			throw new IOException("Could not create directory:" + rootDir.getAbsolutePath());
		}

		generateCode(codeModel, controllers, rootDir);
		generateUnreferencedSchemasV10(codeModel, loadRamlFromFile, rootDir, controllers);

		if (unifiedModel) {
			buildCodeModelToDisk(codeModel, "Unified", rootDir);
		}
	}

	/**
	 * Fetches all referenced type names so as to not generate classes multiple
	 * times
	 * 
	 * @param controllers
	 *            ApiResourceMetadata list
	 * @return set of names
	 */
	private Set<String> getAllReferencedTypeNames(Set<ApiResourceMetadata> controllers) {
		// TODO Add nested objects as well. For now only the top level objects
		// are included
		Set<String> parametersNames = controllers.stream().flatMap(resourceMetadata -> resourceMetadata.getParameters().stream())
				.map(apiParameter -> StringUtils.capitalize(apiParameter.getName())).collect(Collectors.toSet());
		Set<String> bodyNames = controllers.stream().flatMap(resourceMetadata -> resourceMetadata.getDependencies().stream())
				.map(ApiBodyMetadata::getName).collect(Collectors.toSet());
		bodyNames.addAll(parametersNames);
		return bodyNames;
	}

	private void generateUnreferencedSchemasV10(JCodeModel codeModel, RamlRoot loadRamlFromFile, File rootDir,
			Set<ApiResourceMetadata> controllers) {
		if (this.generateUnreferencedSchemas) {
			this.getLog().debug("Generating Code for Unreferenced Types");
			if (loadRamlFromFile.getTypes() != null && !loadRamlFromFile.getTypes().isEmpty()) {
				Set<String> allReferencedTypes = getAllReferencedTypeNames(controllers);
				for (Map.Entry<String, RamlDataType> type : loadRamlFromFile.getTypes().entrySet()) {
					if (!allReferencedTypes.contains(type.getKey())) {
						ApiBodyMetadata tempBodyMetadata = RamlTypeHelper.mapTypeToPojo(codeModel, loadRamlFromFile,
								type.getValue().getType());
						generateModelSources(codeModel, tempBodyMetadata, rootDir);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param codeModel
	 *            If not null this will operated assuming a unified code model
	 *            for all output
	 * @param controllers
	 * @param rootDir
	 */
	private void generateCode(JCodeModel codeModel, Set<ApiResourceMetadata> controllers, File rootDir) {
		for (ApiResourceMetadata met : controllers) {
			this.getLog().debug("");
			this.getLog().debug("-----------------------------------------------------------");
			this.getLog().info("Generating Code for Resource: " + met.getName());
			this.getLog().debug("");

			if (codeModel == null) {
				Set<ApiBodyMetadata> dependencies = met.getDependencies();
				for (ApiBodyMetadata body : dependencies) {
					generateModelSources(codeModel, body, rootDir);
				}
			}

			generateControllerSource(codeModel, met, rootDir);
		}
	}

	/*
	 * @return The configuration property <baseUri> (if set) or the baseUri from
	 * the RAML spec.
	 */
	private String getBasePath(RamlRoot loadRamlFromFile) {
		// we take the given baseUri from raml spec by default.
		String basePath = loadRamlFromFile.getBaseUri();

		// If the baseUri is explicitly set by the plugin configuration we take
		// it.
		if (baseUri != null) {
			basePath = baseUri;
		}

		// Because we can't load an empty string parameter value from maven
		// config
		// the user needs to set a single "/", to overrule the raml spec.
		if (basePath != null && basePath.equals("/")) {
			// We remove a single "/" cause the leading slash will be generated
			// by the raml
			// endpoints.
			basePath = "";
		}

		return basePath;
	}

	@SuppressWarnings("unchecked")
	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> loadRule() {
		Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> ruleInstance = new Spring4ControllerStubRule();
		try {
			ruleInstance = (Rule<JCodeModel, JDefinedClass, ApiResourceMetadata>) getClassRealm().loadClass(rule).newInstance();
			this.getLog().debug(StringUtils.collectionToCommaDelimitedString(ruleConfiguration.keySet()));
			this.getLog().debug(StringUtils.collectionToCommaDelimitedString(ruleConfiguration.values()));

			if (ruleInstance instanceof ConfigurableRule<?, ?, ?> && !CollectionUtils.isEmpty(ruleConfiguration)) {
				this.getLog().debug("SETTING CONFIG");
				((ConfigurableRule<?, ?, ?>) ruleInstance).applyConfiguration(ruleConfiguration);
			}
		} catch (Exception e) {
			getLog().error("Could not instantiate Rule " + this.rule + ". The default Rule will be used for code generation.", e);
		}
		return ruleInstance;
	}

	private ClassRealm getClassRealm() throws DependencyResolutionRequiredException, MalformedURLException {
		if (classRealm == null) {
			List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();

			classRealm = descriptor.getClassRealm();

			if (classRealm == null) {
				classRealm = project.getClassRealm();
			}

			for (String element : runtimeClasspathElements) {
				File elementFile = new File(element);
				classRealm.addURL(elementFile.toURI().toURL());
			}
		}
		return classRealm;
	}

	private void generateModelSources(JCodeModel codeModel, ApiBodyMetadata body, File rootDir) {
		boolean build = false;
		if (codeModel == null) {
			Annotator annotator = this.useJackson1xCompatibility ? new Jackson1Annotator(this.generationConfig) : null;
			this.getLog().info("Generating Model object for: " + body.getName());
			build = true;
			if (this.generationConfig == null && annotator == null) {
				codeModel = body.getCodeModel();
			} else {
				codeModel = body.getCodeModel(resolvedSchemaLocation, basePackage + NamingHelper.getDefaultModelPackage(), annotator);
			}
		}
		if (build && codeModel != null) {
			buildCodeModelToDisk(codeModel, body.getName(), rootDir);
		}
	}

	private String getSchemaLocation() {

		if (StringUtils.hasText(schemaLocation)) {

			if (!schemaLocation.contains(":")) {
				String resolvedPath = project.getBasedir().getAbsolutePath();
				if (resolvedPath.endsWith(File.separator) || resolvedPath.endsWith("/")) {
					resolvedPath = resolvedPath.substring(0, resolvedPath.length() - 1);
				}

				if (!schemaLocation.startsWith(File.separator) && !schemaLocation.startsWith("/")) {
					resolvedPath += File.separator;
				}

				resolvedPath += schemaLocation;

				if (!schemaLocation.endsWith(File.separator) && !schemaLocation.endsWith("/")) {
					resolvedPath += File.separator;
				}
				resolvedPath = resolvedPath.replace(File.separator, "/").replace("\\", "/");
				try {
					URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
					Class<?> urlClass = URLClassLoader.class;
					Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
					method.setAccessible(true);
					method.invoke(urlClassLoader, new Object[] { new File(resolvedPath).toURI().toURL() });
					return "classpath:/"; // since we have added this folder to
											// the classpath this
											// should be used by the plugin
				} catch (Exception ex) {
					this.getLog().error("Could not add schema location to classpath", ex);
					return new File(resolvedPath).toURI().toString();
				}
			}
			return schemaLocation;
		}
		return null;
	}

	private void generateControllerSource(JCodeModel codeModel, ApiResourceMetadata met, File dir) {
		boolean build = false;
		if (codeModel == null) {
			codeModel = new JCodeModel();
			build = true;
		}
		loadRule().apply(met, codeModel);
		if (build) {
			buildCodeModelToDisk(codeModel, met.getName(), dir);
		}
	}

	private void buildCodeModelToDisk(JCodeModel codeModel, String name, File dir) {
		try {
			codeModel.build(dir);
		} catch (IOException e) {
			e.printStackTrace();
			this.getLog().error("Could not build code model for " + name, e);
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		long startTime = System.currentTimeMillis();

		try {
			generateEndpoints();
		} catch (IOException e) {
			throw new MojoExecutionException(e, "Unexpected exception while executing Spring MVC Endpoint Generation Plugin.",
					e.toString());
		} catch (InvalidRamlResourceException e) {
			throw new MojoExecutionException(e, "Supplied RAML has failed validation and cannot be loaded.", e.toString());
		}

		this.getLog().info("Endpoint Generation Complete in:" + (System.currentTimeMillis() - startTime) + "ms");
	}

}
