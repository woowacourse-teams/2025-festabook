package com.daedan.festabook.global.argumentresolver;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class OrganizationIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String ORGANIZATION_ID_HEADER = "organization";
    private final OrganizationJpaRepository organizationJpaRepository;

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
        return validateAndParseOrganizationId(organizationId);
    }

    private Long validateAndParseOrganizationId(String organizationId) {
        if (organizationId == null) {
            throw new BusinessException("Organization 헤더가 누락되었습니다.", HttpStatus.FORBIDDEN);
        }

        Long parsedId;
        try {
            parsedId = Long.parseLong(organizationId);
        } catch (NumberFormatException e) {
            throw new BusinessException("Organization 헤더의 값은 숫자여야 합니다.", HttpStatus.FORBIDDEN);
        }

        if (!organizationJpaRepository.existsById(parsedId)) {
            throw new BusinessException("존재하지 않는 OrganizationId 입니다.", HttpStatus.FORBIDDEN);
        }
        return parsedId;
    }
}
