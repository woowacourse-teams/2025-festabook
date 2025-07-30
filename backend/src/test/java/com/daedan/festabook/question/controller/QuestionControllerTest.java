package com.daedan.festabook.question.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.domain.QuestionFixture;
import com.daedan.festabook.question.dto.QuestionRequest;
import com.daedan.festabook.question.dto.QuestionRequestFixture;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequest;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
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
    private QuestionJpaRepository questionJpaRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Nested
    class createQuestion {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            QuestionRequest request = QuestionRequestFixture.create(
                    "개도 데려갈 수 있나요?",
                    "이 서비스는 페스타북입니다."
            );

            int expectedFieldSize = 5;

            // when & then
            RestAssured
                    .given()
                    .header(ORGANIZATION_HEADER_NAME, organization.getId())
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/questions")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("question", equalTo(request.question()))
                    .body("answer", equalTo(request.answer()))
                    .body("sequence", notNullValue())
                    .body("createdAt", notNullValue());
        }
    }

    @Nested
    class getAllQuestionByOrganizationId {

        @Test
        void 성공_응답_데이터_필드_확인() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Question question = QuestionFixture.create(organization);
            questionJpaRepository.save(question);

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
                    .body("[0].id", equalTo(question.getId().intValue()))
                    .body("[0].question", equalTo(question.getQuestion()))
                    .body("[0].answer", equalTo(question.getAnswer()))
                    .body("[0].sequence", equalTo(question.getSequence()));
        }

        @Test
        void 성공_조직에_해당하는_질문만_조회() {
            // given
            Organization checkOrganization = OrganizationFixture.create();
            Organization otherOrganization = OrganizationFixture.create();
            organizationJpaRepository.saveAll(List.of(checkOrganization, otherOrganization));

            int expectedSize = 2;
            List<Question> questions = QuestionFixture.createList(expectedSize, checkOrganization);
            questionJpaRepository.saveAll(questions);
            int otherSize = 3;
            List<Question> otherQuestions = QuestionFixture.createList(otherSize, otherOrganization);
            questionJpaRepository.saveAll(otherQuestions);

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

            List<Question> questions = QuestionFixture.createList(dateTimes, organization);
            questionJpaRepository.saveAll(questions);

            List<Question> expectedQuestions = questions.stream()
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
                    .body("$", hasSize(questions.size()))
                    .body("[0].id", equalTo(expectedQuestions.get(0).getId().intValue()))
                    .body("[1].id", equalTo(expectedQuestions.get(1).getId().intValue()));
        }
    }

    @Nested
    class updateQuestionAndAnswer {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Question question = QuestionFixture.create(organization);
            questionJpaRepository.save(question);

            QuestionRequest request = QuestionRequestFixture.create(
                    "수정 후 질문입니다.",
                    "수정 후 답변입니다."
            );

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/questions/{questionId}", question.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(question.getId().intValue()))
                    .body("question", equalTo(request.question()))
                    .body("answer", equalTo(request.answer()))
                    .body("sequence", equalTo(question.getSequence()));
        }
    }

    @Nested
    class updateSequence {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Question question1 = QuestionFixture.create(organization, 1);
            Question question2 = QuestionFixture.create(organization, 2);
            Question question3 = QuestionFixture.create(organization, 3);
            questionJpaRepository.saveAll(List.of(question1, question2, question3));

            int changedQuestion1Sequence = 2;
            int changedQuestion2Sequence = 3;
            int changedQuestion3Sequence = 1;
            QuestionSequenceUpdateRequest request1 = new QuestionSequenceUpdateRequest(
                    question1.getId(),
                    changedQuestion1Sequence
            );
            QuestionSequenceUpdateRequest request2 = new QuestionSequenceUpdateRequest(
                    question2.getId(),
                    changedQuestion2Sequence
            );
            QuestionSequenceUpdateRequest request3 = new QuestionSequenceUpdateRequest(
                    question3.getId(),
                    changedQuestion3Sequence
            );
            List<QuestionSequenceUpdateRequest> requests = List.of(request1, request2, request3);

            // when & then
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(requests)
                    .when()
                    .patch("/questions/sequence")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].id", equalTo(question1.getId().intValue()))
                    .body("[0].sequence", equalTo(changedQuestion1Sequence))
                    .body("[1].id", equalTo(question2.getId().intValue()))
                    .body("[1].sequence", equalTo(changedQuestion2Sequence))
                    .body("[2].id", equalTo(question3.getId().intValue()))
                    .body("[2].sequence", equalTo(changedQuestion3Sequence));
        }
    }

    @Nested
    class deleteQuestionByQuestionId {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Question question = QuestionFixture.create(organization);
            questionJpaRepository.save(question);

            // when & then
            RestAssured
                    .given()
                    .when()
                    .delete("/questions/{questionId}", question.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
