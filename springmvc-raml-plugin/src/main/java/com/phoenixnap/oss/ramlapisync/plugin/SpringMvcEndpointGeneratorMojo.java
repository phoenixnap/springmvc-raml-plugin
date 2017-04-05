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
package com.phoenixnap.oss.ramlapisync.plugin;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
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
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson1Annotator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.rules.ConfigurableRule;
import com.phoenixnap.oss.ramlapisync.generation.rules.RamlLoader;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerStubRule;
import com.phoenixnap.oss.ramlapisync.naming.NamingHelper;
import com.phoenixnap.oss.ramlapisync.naming.SchemaHelper;
import com.phoenixnap.oss.ramlapisync.pojo.PojoGenerationConfig;
import com.phoenixnap.oss.ramlapisync.raml.InvalidRamlResourceException;
import com.phoenixnap.oss.ramlapisync.raml.RamlRoot;
import com.phoenixnap.oss.ramlapisync.raml.rjp.raml10v2.RJP10V2RamlRoot;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

import java.io.FileReader;

import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.Model;
/**
 * Maven Plugin MOJO specific to Generation of Spring MVC Endpoints from RAML documents.
 *
 * @author Kurt Paris
 * @since 0.2.1
 */
@Mojo(name = "generate-springmvc-endpoints", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME ,threadSafe = true,requiresProject = false)
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
    @Parameter(property="ramlPath",required = true, readonly = true, defaultValue = "")
    protected String ramlPath;

    /**
     * Path to the pom  document to be verified
     */
    @Parameter(property="pomPath",required = false, readonly = true, defaultValue = "NA")
    protected String pomPath;


    /**
     * Relative file path where the Java files will be saved to
     */
    @Parameter(property="outputRelativePath",required = false, readonly = true, defaultValue = "")
    protected String outputRelativePath;

    /**
     * IF this is set to true, we will only parse methods that consume, produce or accept the
     * requested defaultMediaType
     */
    @Parameter(required = false, readonly = true, defaultValue = "false")
    protected Boolean addTimestampFolder;

    /**
     * Java package to be applied to the generated files
     */
    @Parameter(property="basePackage", required = true, readonly = true, defaultValue = "")
    protected String basePackage;

    /**
     * The URI or relative path to the folder/network location containing JSON Schemas
     */
    @Parameter(required = false, readonly = true, defaultValue = "")
    protected String schemaLocation;


    /**
     * A boolean indicating whether the POJOs for unreferenced schemas defined in the RAML file
     * should be generated. By default, such schemas are not generated.
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
     * If set to true, we will generate seperate methods for different content types in the RAML
     */
    @Parameter(required = false, readonly = true, defaultValue = "false")
    protected Boolean seperateMethodsByContentType;

    /**
     * If set to true, we will generate Jackson 1 annotations inside the model objects
     */
    @Parameter(required = false, readonly = true, defaultValue = "false")
    protected Boolean useJackson1xCompatibility;

    /**
     * The full qualified name of the Rule that should be used for code generation.
     */
    @Parameter(required = false, readonly = true, defaultValue = "com.phoenixnap.oss.ramlapisync.generation.rules.Spring4ControllerStubRule")
    protected String rule;

    /**
     * Map of key/value configuration parameters that can be used to modify behaviour or certain
     * rules
     */
    @Parameter(required = false, readonly = true)
    protected Map<String, String> ruleConfiguration = new LinkedHashMap<>();

    /**
     * Configuration passed to JSONSchema2Pojo for generation of pojos.
     */
    @Parameter(required = false, readonly = true)
    protected JsonShema2PojoGenerationConfig generationConfig = new JsonShema2PojoGenerationConfig();
    
    protected PojoGenerationConfig typeGenerationConfig = mapGenerationConfigMapping();

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

    private ClassRealm classRealm;

    private String resolvedSchemaLocation;



    protected void generateEndpoints()
            throws MojoExecutionException, MojoFailureException, IOException, InvalidRamlResourceException {

     File pomFile = null;
     if(!pomPath.equals("NA")){

      Model model = null;
      FileReader reader = null;
      MavenXpp3Reader mavenreader = new MavenXpp3Reader();
      pomFile = new File(pomPath);
      try {
          reader = new FileReader(pomFile);
          model = mavenreader.read(reader);
          model.setPomFile(pomFile);
       }catch(Exception ex){
       getLog().info("Exception Occured",ex);
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
        }
        else {
            resolvedRamlPath += ramlPath;
        }

        // Resolve schema location and add to classpath
        resolvedSchemaLocation = getSchemaLocation();
        
        RamlRoot loadRamlFromFile = RamlLoader.loadRamlFromFile(new File(resolvedRamlPath).toURI().toString());
        
        JCodeModel codeModel = null;
        //In the RJP10V2 we have support for a unified code model. RJP08V1 does not work well with this.
        //TODO update RJP08V1 to support a unified view.
        boolean unifiedModel = false;
        if (loadRamlFromFile instanceof RJP10V2RamlRoot) {
        	codeModel = new JCodeModel();
        	unifiedModel = true;
        }
        
        //Map the jsconschema2pojo config to ours. This will need to eventually take over. update just in case previous one was set before jsonconfig was set
        typeGenerationConfig = mapGenerationConfigMapping();
        
        RamlParser par = new RamlParser(typeGenerationConfig, getBasePath(loadRamlFromFile), seperateMethodsByContentType, injectHttpHeadersParameter, this.resourceDepthInClassNames);
        Set<ApiResourceMetadata> controllers = par.extractControllers(codeModel, loadRamlFromFile);

        if (StringUtils.hasText(outputRelativePath)) {
            if (!outputRelativePath.startsWith(File.separator) && !outputRelativePath.startsWith("/")) {
                resolvedPath += File.separator;
            }
            resolvedPath += outputRelativePath;
        }
        else {
            resolvedPath += "/target/generated-sources/spring-mvc";
        }

        File rootDir = new File(resolvedPath + (addTimestampFolder == true ? System.currentTimeMillis() : "") + "/");

        if (!rootDir.exists() && !rootDir.mkdirs()) {
            throw new IOException("Could not create directory:" + rootDir.getAbsolutePath());
        }

        generateCode(codeModel, controllers, rootDir);        
        generateUnreferencedSchemas(codeModel, resolvedRamlPath, loadRamlFromFile, rootDir);
        
    	if (unifiedModel) {
    		buildCodeModelToDisk(codeModel, "Unified", rootDir);
    	}
    }


    private void generateUnreferencedSchemas(JCodeModel codeModel, String resolvedRamlPath, RamlRoot loadRamlFromFile, File rootDir) {
        if (this.generateUnreferencedSchemas) {
        	this.getLog().debug("Generating Code for Unreferenced Schemas");

            if (loadRamlFromFile.getSchemas() != null && !loadRamlFromFile.getSchemas().isEmpty()) {
                for (Map<String, String> map : loadRamlFromFile.getSchemas()) {
                    for (String schemaName : map.keySet()) {
                        this.getLog().info("Generating POJO for unreferenced schema " + schemaName);
                        ApiBodyMetadata tempBodyMetadata = SchemaHelper.mapSchemaToPojo(loadRamlFromFile, schemaName, resolvedRamlPath, schemaName, this.resolvedSchemaLocation);
                        // TODO Check if this already has been written to disk
                        generateModelSources(codeModel, tempBodyMetadata, rootDir, this.generationConfig, this.useJackson1xCompatibility == true ? new Jackson1Annotator(this.generationConfig) : null);
                    }
                }
            }
        }
    }

	/**
	 * 
	 * @param codeModel If not null this will operated assuming a unified code model for all output
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
	                generateModelSources(codeModel, body, rootDir, generationConfig, useJackson1xCompatibility == true ? new Jackson1Annotator(generationConfig) : null);
	            }
            }

            generateControllerSource(codeModel, met, rootDir);
        }
    }


    /*
     * @return The configuration property <baseUri> (if set) or the baseUri from the RAML spec.
     */
    private String getBasePath(RamlRoot loadRamlFromFile) {
        // we take the given baseUri from raml spec by default.
        String basePath = loadRamlFromFile.getBaseUri();

        // If the baseUri is explicitly set by the plugin configuration we take it.
        if (baseUri != null) {
            basePath = baseUri;
        }

        // Because we can't load an empty string parameter value from maven config
        // the user needs to set a single "/", to overrule the raml spec.
        if (basePath != null && basePath.equals("/")) {
            // We remove a single "/" cause the leading slash will be generated by the raml
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
        }
        catch (Exception e) {
            getLog().error("Could not instantiate Rule " + this.rule + ". The default Rule will be used for code generation.", e);
        }
        return ruleInstance;
    }


    private ClassRealm getClassRealm()
            throws DependencyResolutionRequiredException, MalformedURLException {
        if (classRealm == null) {
            List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();

            classRealm = descriptor.getClassRealm();

            if (classRealm == null) {
                classRealm = project.getClassRealm();
            }

            for (String element : runtimeClasspathElements)
            {
                File elementFile = new File(element);
                classRealm.addURL(elementFile.toURI().toURL());
            }
        }
        return classRealm;
    }


    private void generateModelSources(JCodeModel codeModel, ApiBodyMetadata body, File rootDir, GenerationConfig config, Annotator annotator) {
    	boolean build = false;
    	if (codeModel == null) {
    		this.getLog().info("Generating Model object for: " + body.getName());
    		build = true;
            if (config == null && annotator == null) {
                codeModel = body.getCodeModel();
            }
            else {
                codeModel = body.getCodeModel(resolvedSchemaLocation, basePackage + NamingHelper.getDefaultModelPackage(), config, annotator);
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
                    return "classpath:/"; // since we have added this folder to the classpath this
                                          // should be used by the plugin
                }
                catch (Exception ex) {
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
        }
        catch (IOException e) {
            e.printStackTrace();
            this.getLog().error("Could not build code model for " + name, e);
        }
	}


    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {
        long startTime = System.currentTimeMillis();

        try {
            generateEndpoints();
        } catch (IOException e) {
            throw new MojoExecutionException(e, "Unexpected exception while executing Spring MVC Endpoint Generation Plugin.",
                    e.toString());
        } catch (InvalidRamlResourceException e) {
            throw new MojoExecutionException(e, "Supplied RAML has failed validation and cannot be loaded.",
                     e.toString());
		}

        this.getLog().info("Endpoint Generation Complete in:" + (System.currentTimeMillis() - startTime) + "ms");
    }
    
    protected PojoGenerationConfig mapGenerationConfigMapping() {
    	PojoGenerationConfig config = new PojoGenerationConfig()
    										.withPackage(basePackage, null);
    	
    	if (generationConfig != null) {
    		config.apply(generationConfig);
    	}
		return config;
    }

}
