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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.raml.model.Raml;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiControllerMetadata;
import com.phoenixnap.oss.ramlapisync.generation.RamlGenerator;
import com.phoenixnap.oss.ramlapisync.generation.RamlParser;
import com.sun.codemodel.JCodeModel;

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
		RamlParser par = new RamlParser(basePackage); 
		RamlGenerator gen = new RamlGenerator(null);
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
			String genX = gen.generateClassForRaml(met, "");
			this.getLog().debug(genX);
			
			Set<ApiBodyMetadata> dependencies = met.getDependencies();
			for (ApiBodyMetadata body : dependencies) {
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
			
			File file = new File(dir.getAbsolutePath() + "\\" + met.getName() + ".java");
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				writer.append(genX);
			} catch (IOException e) {
				this.getLog().error("Could not write java file" + met.getName(), e);
			} finally {
				try {
					writer.close();
				} catch (Exception ex) {
					this.getLog().error("Could not close FileWriter " + met.getName(), ex);
				}
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
