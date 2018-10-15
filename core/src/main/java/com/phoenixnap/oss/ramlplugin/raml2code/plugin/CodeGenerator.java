package com.phoenixnap.oss.ramlplugin.raml2code.plugin;

import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.data.ApiResourceMetadata;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.NamingHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlParser;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.RamlTypeHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.helpers.SchemaHelper;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlDataType;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.raml.raml10.RJP10V2RamlRoot;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.RamlLoader;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Rule;
import com.phoenixnap.oss.ramlplugin.raml2code.rules.Spring4ControllerStubRule;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.Jackson1Annotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CodeGenerator {

	private final Logger logger = LoggerFactory.getLogger(CodeGenerator.class);
	private String resolvedSchemaLocation;

	private final Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> ruleInstance;
	private final MojoConfig config;
	private final File baseDir;

	public CodeGenerator(Rule<JCodeModel, JDefinedClass, ApiResourceMetadata> ruleInstance, MojoConfig config,
			File baseDir) {
		this.ruleInstance = ruleInstance != null ? ruleInstance : new Spring4ControllerStubRule();
		this.config = config;
		this.baseDir = baseDir;;
	}

	protected void generateEndpoints() throws IOException {

		String resolvedPath = baseDir.getAbsolutePath();
		if (resolvedPath.endsWith(File.separator) || resolvedPath.endsWith("/")) {
			resolvedPath = resolvedPath.substring(0, resolvedPath.length() - 1);
		}

		String resolvedRamlPath = baseDir.getAbsolutePath();

		if (!config.getRamlPath().startsWith(File.separator) && !config.getRamlPath().startsWith("/")) {
			resolvedRamlPath += File.separator + config.getRamlPath();
		} else {
			resolvedRamlPath += config.getRamlPath();
		}

		// Resolve schema location and add to classpath
		resolvedSchemaLocation = getSchemaLocation(config.getSchemaLocation());

		RamlRoot loadRamlFromFile = RamlLoader.loadRamlFromFile(new File(resolvedRamlPath).toURI().toString());

		JCodeModel codeModel = null;
		// In the RJP10V2 we have support for a unified code model. RJP08V1 does
		// not work well with this.
		boolean unifiedModel = false;
		if (loadRamlFromFile instanceof RJP10V2RamlRoot) {
			codeModel = new JCodeModel();
			unifiedModel = true;
		}

		Objects.requireNonNull(loadRamlFromFile);
		RamlParser par = new RamlParser(getBasePath(loadRamlFromFile));
		Set<ApiResourceMetadata> controllers = par.extractControllers(codeModel, loadRamlFromFile);

		String outputRelativePath = config.getOutputRelativePath();
		if (StringUtils.hasText(outputRelativePath)) {
			if (!outputRelativePath.startsWith(File.separator) && !outputRelativePath.startsWith("/")) {
				resolvedPath += File.separator;
			}
			resolvedPath += outputRelativePath;
		} else {
			resolvedPath += "/target/generated-sources/spring-mvc";
		}

		File rootDir = new File(
				resolvedPath + (config.getAddTimestampFolder() ? System.currentTimeMillis() : "") + "/");

		if (!rootDir.exists() && !rootDir.mkdirs()) {
			throw new IOException("Could not create directory:" + rootDir.getAbsolutePath());
		}

		generateCode(null, controllers, rootDir);
		if (config.getGenerateUnreferencedObjects()) {
			generateUnreferencedObjects(codeModel, loadRamlFromFile, resolvedRamlPath, rootDir, controllers);
		}

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
		Set<String> parametersNames = controllers.stream()
				.flatMap(resourceMetadata -> resourceMetadata.getParameters().stream())
				.map(apiParameter -> StringUtils.capitalize(apiParameter.getName())).collect(Collectors.toSet());
		Set<String> bodyNames = controllers.stream()
				.flatMap(resourceMetadata -> resourceMetadata.getDependencies().stream()).map(ApiBodyMetadata::getName)
				.collect(Collectors.toSet());
		bodyNames.addAll(parametersNames);
		return bodyNames;
	}

	private void generateUnreferencedObjects(JCodeModel codeModel, RamlRoot loadRamlFromFile, String resolvedRamlPath,
			File rootDir, Set<ApiResourceMetadata> controllers) {
		if (loadRamlFromFile.getTypes() != null && !loadRamlFromFile.getTypes().isEmpty()) {
			this.getLog().debug("Generating Code for Unreferenced Types");
			Set<String> allReferencedTypes = getAllReferencedTypeNames(controllers);
			for (Map.Entry<String, RamlDataType> type : loadRamlFromFile.getTypes().entrySet()) {
				if (!allReferencedTypes.contains(type.getKey())) {
					ApiBodyMetadata tempBodyMetadata = RamlTypeHelper.mapTypeToPojo(codeModel, loadRamlFromFile,
							type.getValue().getType());
					generateModelSources(codeModel, tempBodyMetadata, rootDir);
				}
			}
		}

		if (loadRamlFromFile.getSchemas() != null && !loadRamlFromFile.getSchemas().isEmpty()) {
			this.getLog().debug("Generating Code for Unreferenced Schemas");
			for (Map<String, String> map : loadRamlFromFile.getSchemas()) {
				for (String schemaName : map.keySet()) {
					this.getLog().info("Generating POJO for unreferenced schema " + schemaName);
					ApiBodyMetadata tempBodyMetadata = SchemaHelper.mapSchemaToPojo(loadRamlFromFile, schemaName,
							resolvedRamlPath, schemaName, this.resolvedSchemaLocation);
					generateModelSources(null, tempBodyMetadata, rootDir);
				}
			}
		}
	}

	/**
	 *
	 * @param codeModel
	 *            If not null this will operated assuming a unified code model for
	 *            all output
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
	 * @return The configuration property <baseUri> (if set) or the baseUri from the
	 * RAML spec.
	 */
	private String getBasePath(RamlRoot loadRamlFromFile) {
		// we take the given baseUri from raml spec by default.
		String basePath = loadRamlFromFile.getBaseUri();

		// If the baseUri is explicitly set by the plugin configuration we take
		// it.
		if (config.getBaseUri() != null) {
			basePath = config.getBaseUri();
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

	private void generateModelSources(JCodeModel codeModel, ApiBodyMetadata body, File rootDir) {
		boolean build = false;
		if (codeModel == null) {
			Annotator annotator = config.getUseJackson1xCompatibility()
					? new Jackson1Annotator(config.getGenerationConfig())
					: null;
			this.getLog().info("Generating Model object for: " + body.getName());
			build = true;
			if (config.getGenerationConfig() == null && annotator == null) {
				codeModel = body.getCodeModel();
			} else {
				codeModel = body.getCodeModel(resolvedSchemaLocation,
						config.getBasePackage() + NamingHelper.getDefaultModelPackage(), annotator);
			}
		}
		if (build && codeModel != null) {
			buildCodeModelToDisk(codeModel, body.getName(), rootDir);
		}
	}

	private String getSchemaLocation(String schemaLocation) {
		if (StringUtils.hasText(schemaLocation)) {

			if (!schemaLocation.contains(":")) {
				String resolvedPath = baseDir.getAbsolutePath();
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
					Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
					method.setAccessible(true);
					method.invoke(urlClassLoader, new Object[]{new File(resolvedPath).toURI().toURL()});
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
		ruleInstance.apply(met, codeModel);
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

	private Logger getLog() {
		return logger;
	}

}
