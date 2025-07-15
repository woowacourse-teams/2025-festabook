package com.daedan.festabook.question.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.question.domain.QuestionAnswer;
import com.daedan.festabook.question.domain.QuestionAnswerFixture;
import com.daedan.festabook.question.infrastructure.QuestionAnswerJpaRepository;
import io.restassured.RestAssured;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QuestionControllerTest {

    private static final String ORGANIZATION_HEADER_NAME = "organization";

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private QuestionAnswerJpaRepository questionAnswerJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Nested
    class getAllQuestionAnswerByOrganizationId {

        @Test
        void 성공_응답_데이터_필드_확인() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            QuestionAnswer questionAnswer = QuestionAnswerFixture.create(organization);
            questionAnswerJpaRepository.save(questionAnswer);

            int expectedSize = 1;
            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(questionAnswer.getId().intValue()))
                    .body("[0].title", equalTo(questionAnswer.getTitle()))
                    .body("[0].question", equalTo(questionAnswer.getQuestion()))
                    .body("[0].answer", equalTo(questionAnswer.getAnswer()));
        }

        @Test
        void 성공_조직에_해당하는_질문만_조회() {
            // given
            Organization checkOrganization = OrganizationFixture.create();
            Organization otherOrganization = OrganizationFixture.create();
            organizationJpaRepository.saveAll(List.of(checkOrganization, otherOrganization));

            int expectedSize = 2;
            List<QuestionAnswer> questionAnswers = QuestionAnswerFixture.createList(expectedSize, checkOrganization);
            questionAnswerJpaRepository.saveAll(questionAnswers);
            int otherSize = 3;
            List<QuestionAnswer> otherQuestionAnswers = QuestionAnswerFixture.createList(otherSize, otherOrganization);
            questionAnswerJpaRepository.saveAll(otherQuestionAnswers);

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, checkOrganization.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_날짜_내림차순_데이터() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            List<LocalDateTime> dateTimes = List.of(
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now()
            );

            List<QuestionAnswer> questionAnswers = QuestionAnswerFixture.createList(dateTimes, organization);
            questionAnswerJpaRepository.saveAll(questionAnswers);

            List<QuestionAnswer> expectedQuestionAnswers = questionAnswers.stream()
                    .sorted((qa1, qa2) -> qa2.getCreatedAt().compareTo(qa1.getCreatedAt()))
                    .toList();

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(questionAnswers.size()))
                    .body("[0].id", equalTo(expectedQuestionAnswers.get(0).getId().intValue()))
                    .body("[1].id", equalTo(expectedQuestionAnswers.get(1).getId().intValue()));
        }
    }
}
