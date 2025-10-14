package com.daedan.festabook.question.controller;

import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.global.security.council.CouncilDetails;
import com.daedan.festabook.question.dto.QuestionRequest;
import com.daedan.festabook.question.dto.QuestionResponse;
import com.daedan.festabook.question.dto.QuestionResponses;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequest;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateResponses;
import com.daedan.festabook.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
@Tag(name = "FAQ", description = "FAQ 관련 API")
public class QuestionController {

    private final QuestionService questionService;

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제의 FAQ 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public QuestionResponse createQuestion(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody QuestionRequest request
    ) {
        return questionService.createQuestion(councilDetails.getFestivalId(), request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 모든 FAQ 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public QuestionResponses getAllQuestionByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return questionService.getAllQuestionByFestivalId(festivalId);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PatchMapping("/{questionId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 FAQ 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public QuestionResponse updateQuestionAndAnswer(
            @PathVariable Long questionId,
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody QuestionRequest request
    ) {
        return questionService.updateQuestionAndAnswer(councilDetails.getFestivalId(), questionId, request);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @PatchMapping("/sequences")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "FAQ 순서 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public QuestionSequenceUpdateResponses updateSequence(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody List<QuestionSequenceUpdateRequest> requests
    ) {
        return questionService.updateSequence(councilDetails.getFestivalId(), requests);
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'ADMIN')")
    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 FAQ 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteQuestionByQuestionId(
            @PathVariable Long questionId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        questionService.deleteQuestionByQuestionId(councilDetails.getFestivalId(), questionId);
    }
}
