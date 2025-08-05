package com.daedan.festabook.global.argumentresolver;

import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
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
public class FestivalIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String FESTIVAL_ID_HEADER = "festival";

    private final FestivalJpaRepository festivalJpaRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(FestivalId.class);
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
        String festivalId = webRequest.getHeader(FESTIVAL_ID_HEADER);
        return validateAndParseFestivalId(festivalId);
    }

    private Long validateAndParseFestivalId(String festivalId) {
        validateFestivalIdNotNull(festivalId);
        Long parsedId = parseAndValidateNumeric(festivalId);
        validateFestivalExists(parsedId);

        return parsedId;
    }

    private void validateFestivalIdNotNull(String festivalId) {
        if (festivalId == null) {
            throw new BusinessException("Festival 헤더가 누락되었습니다.", HttpStatus.FORBIDDEN);
        }
    }

    private Long parseAndValidateNumeric(String festivalId) {
        try {
            return Long.parseLong(festivalId);
        } catch (NumberFormatException e) {
            throw new BusinessException("Festival 헤더의 값은 숫자여야 합니다.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateFestivalExists(Long festivalId) {
        if (!festivalJpaRepository.existsById(festivalId)) {
            throw new BusinessException("존재하지 않는 FestivalId 입니다.", HttpStatus.FORBIDDEN);
        }
    }
}
