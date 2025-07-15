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
import io.restassured.http.ContentType;
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
        void 성공_날짜_오름차순_데이터() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            List<QuestionAnswer> questionAnswers = List.of(
                    QuestionAnswerFixture.create(organization, LocalDateTime.now().minusDays(1)),
                    QuestionAnswerFixture.create(organization, LocalDateTime.now())
            );
            questionAnswerJpaRepository.saveAll(questionAnswers);

            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(questionAnswers.size()))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].id", equalTo(questionAnswers.get(1).getId().intValue()))
                    .body("[0].title", equalTo(questionAnswers.get(1).getTitle()))
                    .body("[0].question", equalTo(questionAnswers.get(1).getQuestion()))
                    .body("[0].answer", equalTo(questionAnswers.get(1).getAnswer()))

                    .body("[1].size()", equalTo(expectedFieldSize))
                    .body("[1].id", equalTo(questionAnswers.get(0).getId().intValue()))
                    .body("[1].title", equalTo(questionAnswers.get(0).getTitle()))
                    .body("[1].question", equalTo(questionAnswers.get(0).getQuestion()))
                    .body("[1].answer", equalTo(questionAnswers.get(0).getAnswer()));
        }

        @Test
        void 성공_조직에_해당하는_질문만_조회() {
            // given
            Organization checkOrganization = OrganizationFixture.create();
            organizationJpaRepository.save(checkOrganization);
            Organization otherOrganization = OrganizationFixture.create();
            organizationJpaRepository.save(otherOrganization);

            int expectedSize = 2;
            List<QuestionAnswer> questionAnswers = QuestionAnswerFixture.createList(expectedSize, checkOrganization);
            questionAnswerJpaRepository.saveAll(questionAnswers);
            int otherSize = 3;
            List<QuestionAnswer> otherQuestionAnswers = QuestionAnswerFixture.createList(otherSize, otherOrganization);
            questionAnswerJpaRepository.saveAll(otherQuestionAnswers);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header(ORGANIZATION_HEADER_NAME, checkOrganization.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }
    }
}
