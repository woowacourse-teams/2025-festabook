package com.daedan.festabook.question.controller;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.question.dto.QuestionAnswerResponses;
import com.daedan.festabook.question.service.QuestionAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
@Tag(name = "질문", description = "질문 관련 API")
public class QuestionController {

    private final QuestionAnswerService questionAnswerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 질문 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public QuestionAnswerResponses getAllQuestionAnswerByOrganizationId(
            @OrganizationId Long organizationId
    ) {
        return questionAnswerService.getAllQuestionAnswerByOrganizationIdOrderByCreatedAtDesc(organizationId);
    }
}
