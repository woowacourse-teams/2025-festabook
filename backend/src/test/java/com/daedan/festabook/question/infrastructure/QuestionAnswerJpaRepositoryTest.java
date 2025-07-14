package com.daedan.festabook.question.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.question.domain.QuestionAnswer;
import com.daedan.festabook.question.domain.QuestionAnswerFixture;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class QuestionAnswerJpaRepositoryTest {

    @Autowired
    private QuestionAnswerJpaRepository questionAnswerJpaRepository;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Nested
    class findByOrganizationIdOrderByCreatedAtDesc {

        @Test
        void 성공_내림차순_정렬() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            QuestionAnswer todayQuestionAnswer = QuestionAnswerFixture.create(
                    organization,
                    LocalDateTime.now()
            );
            QuestionAnswer yesterdayQuestionAnswer = QuestionAnswerFixture.create(
                    organization,
                    LocalDateTime.now().minusDays(1)
            );
            QuestionAnswer twoDaysAgoQuestionAnswer = QuestionAnswerFixture.create(
                    organization,
                    LocalDateTime.now().minusDays(2)
            );
            questionAnswerJpaRepository.saveAll(List.of(
                    twoDaysAgoQuestionAnswer,
                    yesterdayQuestionAnswer,
                    todayQuestionAnswer
            ));

            // when
            List<QuestionAnswer> questionAnswers =
                    questionAnswerJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organization.getId());

            // then
            assertThat(questionAnswers)
                    .isSortedAccordingTo(Comparator.comparing(QuestionAnswer::getCreatedAt).reversed());
        }
    }
}
