package com.phoenixnap.oss.ramlapisync.data;

import org.junit.Test;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ApiParameterMetadataTest {
    private static final String PATH_VARIABLE_WITH_VALUE_SPECIFIED = "pathVariableWithValueSpecified";
    private static final String PATH_VARIABLE_WITH_NAME_SPECIFIED = "pathVariableWithNameSpecified";
    private static final String REQUEST_PARAM_WITH_VALUE_SPECIFIED = "requestParamWithValueSpecified";
    private static final String REQUEST_PARAM_WITH_NAME_SPECIFIED = "requestParamWithNameSpecified";

    @Test
    public void pathVariableValueShouldBeResolvedAsName() throws Exception {
        // given

        // when
        ApiParameterMetadata apiParameterMetadata = new ApiParameterMetadata(Resource.class.getMethods()[0].getParameters()[0]);

        // then
        assertThat(apiParameterMetadata.getName(), equalTo(PATH_VARIABLE_WITH_VALUE_SPECIFIED));
    }

    @Test
    public void pathVariableNameShouldBeResolvedAsName() throws Exception {
        // given

        // when
        ApiParameterMetadata apiParameterMetadata = new ApiParameterMetadata(Resource.class.getMethods()[0].getParameters()[1]);

        // then
        assertThat(apiParameterMetadata.getName(), equalTo(PATH_VARIABLE_WITH_NAME_SPECIFIED));
    }

    @Test
    public void requestParamValueShouldBeResolvedAsName() throws Exception {
        // given

        // when
        ApiParameterMetadata apiParameterMetadata = new ApiParameterMetadata(Resource.class.getMethods()[0].getParameters()[2]);

        // then
        assertThat(apiParameterMetadata.getName(), equalTo(REQUEST_PARAM_WITH_VALUE_SPECIFIED));
    }

    @Test
    public void requestParamNameShouldBeResolvedAsName() throws Exception {
        // given

        // when
        ApiParameterMetadata apiParameterMetadata = new ApiParameterMetadata(Resource.class.getMethods()[0].getParameters()[3]);

        // then
        assertThat(apiParameterMetadata.getName(), equalTo(REQUEST_PARAM_WITH_NAME_SPECIFIED));
    }

    private interface Resource {
        void resourceMethod(@PathVariable(PATH_VARIABLE_WITH_VALUE_SPECIFIED) String pathVariableWithValueSpecified,
                            @PathVariable(name = PATH_VARIABLE_WITH_NAME_SPECIFIED) String pathVariableWithNameSpecified,
                            @RequestParam(REQUEST_PARAM_WITH_VALUE_SPECIFIED) String requestParamWithValueSpecified,
                            @RequestParam(name = REQUEST_PARAM_WITH_NAME_SPECIFIED) String requestParamWithNameSpecified);
    }
}
