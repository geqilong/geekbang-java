package org.geektimes.microprofile.rest.uri;

import org.geektimes.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;

public class PathParamUriBuilderAssembler implements UriBuilderAssembler<PathParam> {
    @Override
    public void assemble(UriBuilder uriBuilder, AnnotatedParamMetadata metadata, Object[] args) {
        String paramName = metadata.getParamName();
        int paramIndex = metadata.getParameterIndex();
        Object paramValue = args[paramIndex];
        if (paramValue == null) {
            paramValue = metadata.getDefaultValue();
        }
        uriBuilder.resolveTemplate(paramName, paramValue);
    }
}
