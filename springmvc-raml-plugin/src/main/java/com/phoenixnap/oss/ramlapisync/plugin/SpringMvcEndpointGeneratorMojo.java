/*
 * Copyright 2002-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.plugin;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.phoenixnap.oss.ramlapisync.generation.serialize.ApiControllerMetadataSerializer;
import com.sun.codemodel.JCodeModel;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.raml.model.Raml;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

/**
 * Maven Plugin MOJO specific to Generation of Spring MVC Endpoints from RAML documents.
 * 
 * @author Kurt Paris
 * @since 0.2.1
 */
@Mojo(name = "generate-springmvc-endpoints", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class SpringMvcEndpointGeneratorMojo extends AbstractMojo {

	/**
	 * Maven project - required for project info
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;

	@Component
	private PluginDescriptor descriptor;
	
	/**
	 * Path to the raml document to be verified
	 */
	@Parameter(required = true, readonly = true, defaultValue = "")
	protected String ramlPath;
	
	/**
	 * Relative file path where the Java files will be saved to
	 */
	@Parameter(required = false, readonly = true, defaultValue = "")
	protected String outputRelativePath;
	
	/**
	 * IF this is set to true, we will only parse methods that consume, produce or accept the requested defaultMediaType
	 */
	@Parameter(required = false, readonly = true, defaultValue = "false")
	protected Boolean addTimestampFolder;
	
	/**
	 * Java package to be applied to the generated files
	 */
	@Parameter(required = true, readonly = true, defaultValue = "")
	protected String basePackage;

	/**
	 * The explicit base path under which the rest endpoints should be located.
	 * If overrules the baseUri setting in the raml spec.
	 */
	@Parameter(required = false, readonly = true)
	protected String baseUri;

	/**
	 * The full qualified name of the RamlGenerator that should be used.
	 */
	@Parameter(required = false, readonly = true, defaultValue = "com.phoenixnap.oss.ramlapisync.generation.RamlGenerator")
	protected String ramlGenerator;

	private ClassRealm classRealm;

	protected void generateEndpoints() throws MojoExecutionException, MojoFailureException, IOException {
		
		String resolvedPath = project.getBasedir().getAbsolutePath();
		if (resolvedPath.endsWith(File.separator) || resolvedPath.endsWith("/")) {
			resolvedPath = resolvedPath.substring(0, resolvedPath.length()-1);
		}
		String resolvedRamlPath = project.getBasedir().getAbsolutePath();
		if (!ramlPath.startsWith(File.separator) && !ramlPath.startsWith("/")) {
			resolvedRamlPath += File.separator + ramlPath;
		} else {
			resolvedRamlPath += ramlPath;
		}
		
		Raml loadRamlFromFile = RamlParser.loadRamlFromFile( "file:"+resolvedRamlPath );
		RamlParser par = new RamlParser(basePackage, getBasePath(loadRamlFromFile));
		RamlGenerator gen = createRamlGenerator();
		Set<ApiControllerMetadata> controllers = par.extractControllers(loadRamlFromFile);

		if (StringUtils.hasText(outputRelativePath)) {
			if (!outputRelativePath.startsWith(File.separator) && !outputRelativePath.startsWith("/")) {
				resolvedPath += File.separator;
			}
			resolvedPath += outputRelativePath;
		} else {
			resolvedPath += "/generated-sources/";
		}
		
		
		File rootDir = new File (resolvedPath + (addTimestampFolder == true ? System.currentTimeMillis() : "") + "/");
		File dir = new File (resolvedPath + (addTimestampFolder == true ? System.currentTimeMillis() : "") + "/" + basePackage.replace(".", "/") + "/");
		dir.mkdirs();
		
		for (ApiControllerMetadata met :controllers) {			
			this.getLog().debug("");
			this.getLog().debug("-----------------------------------------------------------");
			this.getLog().debug(met.getName());
			this.getLog().debug("");

			Set<ApiBodyMetadata> dependencies = met.getDependencies();
			for (ApiBodyMetadata body : dependencies) {
				generateModelSources(met, body, rootDir);
			}

			List<ApiControllerMetadataSerializer> serializers = gen.generateClassForRaml(met, "");
			for(ApiControllerMetadataSerializer serializer: serializers) {
				generateControllerSource(serializer, dir);
			}
		}
		
		
	}

	/*
	 * @return The configuration property <baseUri> (if set) or the baseUri from the RAML spec.
     */
	private String getBasePath(Raml loadRamlFromFile) {
		return baseUri != null ? baseUri : loadRamlFromFile.getBasePath();
	}

	private RamlGenerator createRamlGenerator() {
		RamlGenerator generator = new RamlGenerator();
		try {
			generator = (RamlGenerator) getClassRealm().loadClass(ramlGenerator).newInstance();
		} catch (Exception e) {
			getLog().error("Could not instantiate RamlGenerator "+ramlGenerator +". The default RamlGenerator will be used.", e);
		}
		return generator;
	}

	private ClassRealm getClassRealm() throws DependencyResolutionRequiredException, MalformedURLException {
		if(classRealm == null) {
			List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
			classRealm = descriptor.getClassRealm();
			for (String element : runtimeClasspathElements)
			{
				File elementFile = new File(element);
				classRealm.addURL(elementFile.toURI().toURL());
			}
		}
		return classRealm;
	}

	private void generateModelSources(ApiControllerMetadata met, ApiBodyMetadata body, File rootDir) {
		try {
            JCodeModel codeModel = body.getCodeModel();
            if (codeModel != null) {
                codeModel.build(rootDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.getLog().error("Could not build code model for " + met.getName(), e);
        }
	}

	private void generateControllerSource(ApiControllerMetadataSerializer serializer, File dir) {
		String genX = serializer.serialize();
		this.getLog().debug(genX);
		String javaFileName = serializer.getName() + ".java";
		File file = new File(dir.getAbsolutePath() + "\\" + javaFileName);
		FileWriter writer = null;
		try {
            writer = new FileWriter(file);
            writer.append(genX);
        } catch (IOException e) {
            this.getLog().error("Could not write java file " + javaFileName, e);
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                this.getLog().error("Could not close FileWriter " + javaFileName, ex);
            }
        }
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		long startTime = System.currentTimeMillis();
		
		try {
			generateEndpoints();
		} catch (IOException e) {
			ClassLoaderUtils.restoreOriginalClassLoader();
			throw new MojoExecutionException(e, "Unexpected exception while executing Spring MVC Endpoint Generation Plugin.",
					e.toString());
		}
		
		this.getLog().info("Endpoint Generation Complete in:" + (System.currentTimeMillis() - startTime) + "ms");
	}

}
