package org.geektimes.microprofile.rest.uri;

import org.geektimes.commons.reflect.util.TypeUtils;
import org.geektimes.microprofile.rest.RequestTemplate;
import org.geektimes.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.util.List;

public interface UriBuilderAssembler<A extends Annotation> {

    default void assemble(UriBuilder uriBuilder, RequestTemplate requestTemplate, Object[] args){
        assemble(uriBuilder, requestTemplate.getAnnotatedParamMetadata(getAnnotationType()), args);
    }

    default void assemble(UriBuilder uriBuilder, List<AnnotatedParamMetadata> annotatedParamMetadata, Object[] args){
        for (AnnotatedParamMetadata metadata : annotatedParamMetadata){
            assemble(uriBuilder, metadata, args);
        }
    }

    void assemble(UriBuilder uriBuilder, AnnotatedParamMetadata metadata, Object[] args);

    default Class<A> getAnnotationType() {
        List<Class<?>> typeArguments = TypeUtils.resolveTypeArguments(this.getClass());
        return (Class<A>) typeArguments.get(0);
    }

}
