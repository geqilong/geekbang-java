package org.geektimes.microprofile.rest.uri;

import org.geektimes.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.UriBuilder;

public class MatrixParamUriBuilderAssembler implements UriBuilderAssembler<MatrixParam> {

    @Override
    public void assemble(UriBuilder uriBuilder, AnnotatedParamMetadata metadata, Object[] args) {
        String paramName = metadata.getParamName();
        int paramIndex = metadata.getParameterIndex();
        Object paramValue = args[paramIndex];
        uriBuilder.matrixParam(paramName, paramValue);
    }
}
