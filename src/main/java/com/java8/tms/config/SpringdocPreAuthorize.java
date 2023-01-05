package com.java8.tms.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@Configuration
public class SpringdocPreAuthorize {
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            Optional<PreAuthorize> preAuthorizeAnnotation = Optional.ofNullable(handlerMethod.getMethodAnnotation(PreAuthorize.class));
            StringBuilder sb = new StringBuilder();
            if (preAuthorizeAnnotation.isPresent()) {
                sb.append("This api requires **")
                        .append((preAuthorizeAnnotation.get()).value().replaceAll("hasAuthority|\\(|\\)|\\'", ""))
                        .append("** permission.");
            } else {
                sb.append("This api is **public**");
            }
            sb.append("<br /><br />");
            if (operation.getDescription() != null){
                sb.append(operation.getDescription());
            }
            operation.setDescription(sb.toString());
            return operation;
        };
    }
}