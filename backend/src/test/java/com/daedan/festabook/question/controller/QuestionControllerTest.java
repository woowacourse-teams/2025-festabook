package com.daedan.festabook.question.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class QuestionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private QuestionAnswerJpaRepository questionAnswerJpaRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Nested
    class getAllQuestionAnswerByOrganizationId {

        @Test
        void 성공_조직에_등록된_모든_질문_조회() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            List<QuestionAnswer> questionAnswers = List.of(
                    QuestionAnswerFixture.create(organization, LocalDateTime.now().minusDays(1)),
                    QuestionAnswerFixture.create(organization, LocalDateTime.now())
            );
            questionAnswerJpaRepository.saveAll(questionAnswers);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header("organization", organization.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(questionAnswers.size()))
                    .body("[0].id", is(questionAnswers.get(1).getId().intValue()))
                    .body("[0].title", is(questionAnswers.get(1).getTitle()))
                    .body("[0].question", is(questionAnswers.get(1).getQuestion()))
                    .body("[0].answer", is(questionAnswers.get(1).getAnswer()))

                    .body("[1].id", is(questionAnswers.get(0).getId().intValue()))
                    .body("[1].title", is(questionAnswers.get(0).getTitle()))
                    .body("[1].question", is(questionAnswers.get(0).getQuestion()))
                    .body("[1].answer", is(questionAnswers.get(0).getAnswer()));
        }

        @Test
        void 성공_조직에_해당하는_질문만_조회() {
            // given
            Organization checkOrganization = OrganizationFixture.create();
            organizationJpaRepository.save(checkOrganization);

            Organization otherOrganization = OrganizationFixture.create();
            organizationJpaRepository.save(otherOrganization);

            int expectedSize = 1;
            List<QuestionAnswer> questionAnswers = List.of(
                    QuestionAnswerFixture.create(checkOrganization),
                    QuestionAnswerFixture.create(otherOrganization)
            );
            questionAnswerJpaRepository.saveAll(questionAnswers);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .header("organization", checkOrganization.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].id", is(questionAnswers.get(0).getId().intValue()))
                    .body("[0].title", is(questionAnswers.get(0).getTitle()))
                    .body("[0].question", is(questionAnswers.get(0).getQuestion()))
                    .body("[0].answer", is(questionAnswers.get(0).getAnswer()));
        }
    }
}
