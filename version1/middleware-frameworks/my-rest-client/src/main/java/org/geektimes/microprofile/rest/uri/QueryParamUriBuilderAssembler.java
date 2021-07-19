package org.geektimes.microprofile.rest.uri;

import org.geektimes.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

public class QueryParamUriBuilderAssembler implements UriBuilderAssembler<QueryParam> {
    @Override
    public void assemble(UriBuilder uriBuilder, AnnotatedParamMetadata metadata, Object[] args) {
        String paramName = metadata.getParamName();
        int paramIndex = metadata.getParameterIndex();
        Object paramValue = args[paramIndex];
        uriBuilder.queryParam(paramName, paramValue);
    }
}
