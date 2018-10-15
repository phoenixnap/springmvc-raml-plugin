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

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.exception.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Maven Plugin MOJO specific to Generation of Spring MVC Endpoints from RAML
 * documents.
 *
 * @author Kurt Paris
 * @since 0.2.1
 */
@Mojo(name = "generate-springmvc-endpoints", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true, requiresProject = false)
public class SpringMvcEndpointGeneratorMojo extends AbstractMojo implements MojoConfig {

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
	 * IF this is set to true, we will only parse methods that consume, produce or
	 * accept the requested defaultMediaType
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
	 * A boolean indicating whether the POJOs for unreferenced objects (schemas and
	 * data types) defined in the RAML file should be generated. By default, such
	 * schemas/types are not generated.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean generateUnreferencedObjects;

	/**
	 * The explicit base path under which the rest endpoints should be located. If
	 * overrules the baseUri setting in the raml spec.
	 */
	@Parameter(required = false, readonly = true)
	protected String baseUri;

	/**
	 * If set to true, we will generate seperate methods for different content types
	 * in the RAML
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
	 * The full qualified name of the Rule that should be used for code generation.
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
	 * How many levels of uri will be included in generated class names. Default is
	 * 1 which means that only current resource will be in included in
	 * controller/decorator names.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "1")
	protected Integer resourceDepthInClassNames;

	/**
	 * Top level of URI included in generated class names. Default is 0 which means
	 * that all resources will be in included in controller/decorator names.
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

	/**
	 * Logic used for Java methods name generation. Possible values:
	 * <ul>
	 * <li>OBJECTS - objects like request parameters and return types will be
	 * used</li>
	 * <li>RESOURCES - resource path will be used</li>
	 * </ul>
	 * Default is OBJECTS.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "OBJECTS")
	protected MethodsNamingLogic methodsNamingLogic;

	/**
	 * The way to override naming logic for Java methods and arguments. Possible
	 * values:
	 * <ul>
	 * <li>DISPLAY_NAME - "displayName" attribute (if found) will be cleaned and
	 * used</li>
	 * <li>ANNOTATION - "javaName" annotation (if found) will be used as is</li>
	 * </ul>
	 * No default.
	 */
	@Parameter(required = false, readonly = true)
	protected OverrideNamingLogicWith overrideNamingLogicWith;

	/**
	 * Skip code generation for endpoints (resources and methods) annotated with
	 * this annotation.
	 */
	@Parameter(required = false, readonly = true)
	protected String dontGenerateForAnnotation;

	private ClassRealm classRealm;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		long startTime = System.currentTimeMillis();

		try {
			generateEndpoints();
		} catch (IOException e) {
			throw new MojoExecutionException(e,
					"Unexpected exception while executing Spring MVC Endpoint Generation Plugin.", e.toString());
		} catch (InvalidRamlResourceException e) {
			throw new MojoExecutionException(e, "Supplied RAML has failed validation and cannot be loaded.",
					e.toString());
		}

		this.getLog().info("Endpoint Generation Completed in:" + (System.currentTimeMillis() - startTime) + "ms");
	}

	private void generateEndpoints() throws IOException {

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

		CodeGenerator codeGenerator = new CodeGenerator(createRule(), this, project.getBasedir());
		codeGenerator.generateEndpoints();
	}

	@SuppressWarnings("unchecked")
	private Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> createRule() {
		Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> ruleInstance = new Spring4ControllerStubRule();
		try {
			ruleInstance = (Rule<JCodeModel, JDefinedClass, ApiResourceMetadata>) getClassRealm().loadClass(rule)
					.newInstance();
		} catch (Exception e) {
			getLog().error(
					"Could not instantiate Rule " + this.rule + ". The default Rule will be used for code generation.",
					e);
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

	@Override
	public String getRamlPath() {
		return ramlPath;
	}

	@Override
	public String getOutputRelativePath() {
		return outputRelativePath;
	}

	@Override
	public Boolean getAddTimestampFolder() {
		return addTimestampFolder;
	}

	@Override
	public String getBasePackage() {
		return basePackage;
	}

	@Override
	public Boolean getGenerateUnreferencedObjects() {
		return generateUnreferencedObjects;
	}

	@Override
	public String getBaseUri() {
		return baseUri;
	}

	@Override
	public Boolean getSeperateMethodsByContentType() {
		return seperateMethodsByContentType;
	}

	@Override
	public Boolean getUseJackson1xCompatibility() {
		return useJackson1xCompatibility;
	}

	@Override
	public String getRule() {
		return rule;
	}

	@Override
	public Map<String, String> getRuleConfiguration() {
		return ruleConfiguration;
	}

	@Override
	public PojoGenerationConfig getGenerationConfig() {
		return generationConfig;
	}

	@Override
	public Boolean getInjectHttpHeadersParameter() {
		return injectHttpHeadersParameter;
	}

	@Override
	public Integer getResourceDepthInClassNames() {
		return resourceDepthInClassNames;
	}

	@Override
	public Integer getResourceTopLevelInClassNames() {
		return resourceTopLevelInClassNames;
	}

	@Override
	public Boolean getReverseOrderInClassNames() {
		return reverseOrderInClassNames;
	}

	@Override
	public MethodsNamingLogic getMethodsNamingLogic() {
		return methodsNamingLogic;
	}

	@Override
	public OverrideNamingLogicWith getOverrideNamingLogicWith() {
		return overrideNamingLogicWith;
	}

	@Override
	public String getDontGenerateForAnnotation() {
		return dontGenerateForAnnotation;
	}

	@Override
	public String getSchemaLocation() {
		return schemaLocation;
	}
}
