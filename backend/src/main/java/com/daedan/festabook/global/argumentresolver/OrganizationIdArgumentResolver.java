package com.daedan.festabook.global.argumentresolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class OrganizationIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String ORGANIZATION_ID_HEADER = "organization";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(OrganizationId.class);
        boolean hasType = parameter.getParameterType().equals(Long.class);
        return hasAnnotation && hasType;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String organizationId = webRequest.getHeader(ORGANIZATION_ID_HEADER);
        validateNull(organizationId);
        return Long.parseLong(organizationId);
    }

    // TODO : 커스텀 예외 등록
    private void validateNull(String organizationId) {
        if (organizationId == null) {
            throw new RuntimeException();
        }
    }
}
