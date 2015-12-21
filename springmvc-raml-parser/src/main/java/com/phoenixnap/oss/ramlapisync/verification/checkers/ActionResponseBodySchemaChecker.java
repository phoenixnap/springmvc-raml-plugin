package com.phoenixnap.oss.ramlapisync.verification.checkers;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.phoenixnap.oss.ramlapisync.naming.Pair;
import com.phoenixnap.oss.ramlapisync.verification.Issue;
import com.phoenixnap.oss.ramlapisync.verification.IssueLocation;
import com.phoenixnap.oss.ramlapisync.verification.IssueSeverity;
import com.phoenixnap.oss.ramlapisync.verification.IssueType;
import com.phoenixnap.oss.ramlapisync.verification.RamlActionVisitorCheck;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

/**
 * A checker that will check the response body declared in an API and the supplied implementation
 * 
 * @author Kurt Paris
 * @since 0.1.1
 *
 */
public class ActionResponseBodySchemaChecker implements RamlActionVisitorCheck {
	
	public static String RESPONSE_BODY_DIFFERENTSIZE = "Response Body in target has a different number of fields to reference";
	public static String RESPONSE_BODY_FIELDDIFFERENTORMISSING = "Response Body has a missing field or type is different: ";
	public static String RESPONSE_BODY_MISSING = "Response Body required but not found in target";

	/**
	 * Class Logger
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ActionResponseBodySchemaChecker.class);

	@Override
	public Pair<Set<Issue>, Set<Issue>> check(ActionType name, Action reference, Action target, IssueLocation location, IssueSeverity maxSeverity) {
		logger.debug("Checking action " + name);
		Set<Issue> errors = new LinkedHashSet<>();
		Set<Issue> warnings = new LinkedHashSet<>();
		Issue issue;
		
		//Only apply this checker in the contract
		if (location.equals(IssueLocation.CONTRACT)) {
			return new Pair<>(warnings, errors);
		}
		
		
		//Now the response
		if (reference.getResponses() != null 
				&& !reference.getResponses().isEmpty() 
				&& reference.getResponses().containsKey("200")
				&& reference.getResponses().get("200").getBody() != null) {
			//successful response
			Map<String, MimeType> response = reference.getResponses().get("200").getBody();
			for (Entry<String, MimeType> responseBodyMime : response.entrySet()) {
				MimeType value = responseBodyMime.getValue();
				if (StringUtils.hasText(value.getSchema())) {
					logger.debug("Found body for mime type " + responseBodyMime.getKey());
					String targetSchema = null;
					try {
						//lets assume the implementation contains exactly what we're looking for
						targetSchema = target.getResponses().get("200").getBody().get(responseBodyMime.getKey()).getSchema();
					} catch (NullPointerException npe) {				
						try {
							// lets try the first response we get
							targetSchema = target.getResponses().get("200").getBody().values().iterator().next().getSchema();
						} catch (Exception ex) {
							issue = new Issue(maxSeverity, location, IssueType.MISSING, RESPONSE_BODY_MISSING, reference.getResource(), reference);
							RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, RESPONSE_BODY_MISSING + " " + responseBodyMime.getKey());
							break;
						}
					}
					
					//just a sanity check in case someone removes the break
					if (targetSchema != null) {
						JCodeModel referenceCodeModel = new JCodeModel();
						JCodeModel targetCodeModel = new JCodeModel();


						GenerationConfig config = new DefaultGenerationConfig() {
							@Override
							public boolean isGenerateBuilders() { // set config
																	// option by
																	// overriding
																	// method
								return false;
							}
						};

						try {
							SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(), new SchemaStore()), new SchemaGenerator());
							mapper.generate(referenceCodeModel, "Reference", "com.response", value.getSchema());
							mapper.generate(targetCodeModel, "Target", "com.response", targetSchema);
	
							Map<String, JFieldVar> referenceResponseFields = referenceCodeModel._getClass("com.response.Reference").fields();
							Map<String, JFieldVar> targetResponseClassFields = targetCodeModel._getClass("com.response.Target").fields();
							
							if (referenceResponseFields.size() != targetResponseClassFields.size()) {
								issue = new Issue(maxSeverity, location, IssueType.MISSING, RESPONSE_BODY_DIFFERENTSIZE, reference.getResource(), reference);
								RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, RESPONSE_BODY_DIFFERENTSIZE + " " + responseBodyMime.getKey());
							}
							
							for (Entry<String, JFieldVar> referenceField : referenceResponseFields.entrySet()) {
								String fieldKey = referenceField.getKey();
								JFieldVar targetField = targetResponseClassFields.get(fieldKey);
								if (targetField == null 
										|| !referenceField.getValue().type().fullName().equals(targetField.type().fullName())) {
									issue = new Issue(maxSeverity, location, IssueType.MISSING, RESPONSE_BODY_FIELDDIFFERENTORMISSING + " " + fieldKey, reference.getResource(), reference);
									RamlCheckerResourceVisitorCoordinator.addIssue(errors, warnings, issue, RESPONSE_BODY_FIELDDIFFERENTORMISSING + " " + fieldKey);
								} 								
							}
							
							
						} catch (IOException ex) {
							logger.error("Error during Schema to POJO generation");
						}
					}
					
				}
			}
			
				
		}
			
		return new Pair<>(warnings, errors);
	}
	

	
	
}
