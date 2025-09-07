package com.daedan.festabook.question.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.JwtTestHelper;
import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.domain.QuestionFixture;
import com.daedan.festabook.question.dto.QuestionRequest;
import com.daedan.festabook.question.dto.QuestionRequestFixture;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequest;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequestFixture;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
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

    private static final String FESTIVAL_HEADER_NAME = "festival";

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private JwtTestHelper jwtTestHelper;

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
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Question question = QuestionFixture.create(festival);
            questionJpaRepository.save(question);

            Integer count = questionJpaRepository.findMaxSequenceByFestivalId(festival.getId())
                    .orElseGet(() -> 0);

            QuestionRequest request = QuestionRequestFixture.create(
                    "개도 데려갈 수 있나요?",
                    "이 서비스는 페스타북입니다."
            );

            int expectedFieldSize = 4;
            int expectedSequence = count + 1;

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/questions")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("size()", equalTo(expectedFieldSize))
                    .body("question", equalTo(request.question()))
                    .body("answer", equalTo(request.answer()))
                    .body("sequence", equalTo(expectedSequence));
        }
    }

    @Nested
    class getAllQuestionByFestivalId {

        @Test
        void 성공_응답_데이터_필드_확인() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Question question = QuestionFixture.create(festival);
            questionJpaRepository.save(question);

            int expectedSize = 1;
            int expectedFieldSize = 4;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].size()", equalTo(expectedFieldSize))
                    .body("[0].questionId", equalTo(question.getId().intValue()))
                    .body("[0].question", equalTo(question.getQuestion()))
                    .body("[0].answer", equalTo(question.getAnswer()))
                    .body("[0].sequence", equalTo(question.getSequence()));
        }

        @Test
        void 성공_축제에_해당하는_질문만_조회() {
            // given
            Festival checkFestival = FestivalFixture.create();
            Festival otherFestival = FestivalFixture.create();
            festivalJpaRepository.saveAll(List.of(checkFestival, otherFestival));

            int expectedSize = 2;
            List<Question> questions = QuestionFixture.createList(expectedSize, checkFestival);
            questionJpaRepository.saveAll(questions);
            int otherSize = 3;
            List<Question> otherQuestions = QuestionFixture.createList(otherSize, otherFestival);
            questionJpaRepository.saveAll(otherQuestions);

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, checkFestival.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize));
        }

        @Test
        void 성공_Sequence_오름차순_정렬() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Question question2 = QuestionFixture.create(festival, 2);
            Question question1 = QuestionFixture.create(festival, 1);
            questionJpaRepository.saveAll(List.of(question2, question1));

            int expectedSize = 2;

            // when & then
            RestAssured
                    .given()
                    .header(FESTIVAL_HEADER_NAME, festival.getId())
                    .when()
                    .get("/questions")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasSize(expectedSize))
                    .body("[0].questionId", equalTo(question1.getId().intValue()))
                    .body("[1].questionId", equalTo(question2.getId().intValue()));
        }
    }

    @Nested
    class updateQuestionAndAnswer {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Question question = QuestionFixture.create(festival);
            questionJpaRepository.save(question);

            QuestionRequest request = QuestionRequestFixture.create(
                    "수정 후 질문입니다.",
                    "수정 후 답변입니다."
            );

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch("/questions/{questionId}", question.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("questionId", equalTo(question.getId().intValue()))
                    .body("question", equalTo(request.question()))
                    .body("answer", equalTo(request.answer()));
        }
    }

    @Nested
    class updateSequence {

        @Test
        void 성공_수정_후에도_오름차순으로_재정렬() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Question question1 = QuestionFixture.create(festival, 1);
            Question question2 = QuestionFixture.create(festival, 2);
            Question question3 = QuestionFixture.create(festival, 3);
            questionJpaRepository.saveAll(List.of(question1, question2, question3));

            QuestionSequenceUpdateRequest request1 = QuestionSequenceUpdateRequestFixture.create(question1.getId(), 2);
            QuestionSequenceUpdateRequest request2 = QuestionSequenceUpdateRequestFixture.create(question2.getId(), 3);
            QuestionSequenceUpdateRequest request3 = QuestionSequenceUpdateRequestFixture.create(question3.getId(), 1);
            List<QuestionSequenceUpdateRequest> requests = List.of(request1, request2, request3);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .contentType(ContentType.JSON)
                    .body(requests)
                    .when()
                    .patch("/questions/sequences")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].questionId", equalTo(question3.getId().intValue()))
                    .body("[0].sequence", equalTo(1))

                    .body("[1].questionId", equalTo(question1.getId().intValue()))
                    .body("[1].sequence", equalTo(2))

                    .body("[2].questionId", equalTo(question2.getId().intValue()))
                    .body("[2].sequence", equalTo(3));
        }
    }

    @Nested
    class deleteQuestionByQuestionId {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Header authorizationHeader = jwtTestHelper.createAuthorizationHeader(festival);

            Question question = QuestionFixture.create(festival);
            questionJpaRepository.save(question);

            // when & then
            RestAssured
                    .given()
                    .header(authorizationHeader)
                    .when()
                    .delete("/questions/{questionId}", question.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            assertThat(questionJpaRepository.findById(question.getId())).isEmpty();
        }
    }
}
