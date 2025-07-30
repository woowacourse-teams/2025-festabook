package com.daedan.festabook.question.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.domain.QuestionFixture;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class QuestionJpaRepositoryTest {

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Nested
    class findByOrganizationIdOrderByCreatedAtDesc {

        @Test
        void 성공_내림차순_정렬() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Question todayQuestion = QuestionFixture.create(
                    organization,
                    LocalDateTime.now()
            );
            Question yesterdayQuestion = QuestionFixture.create(
                    organization,
                    LocalDateTime.now().minusDays(1)
            );
            Question twoDaysAgoQuestion = QuestionFixture.create(
                    organization,
                    LocalDateTime.now().minusDays(2)
            );
            questionJpaRepository.saveAll(List.of(
                    twoDaysAgoQuestion,
                    yesterdayQuestion,
                    todayQuestion
            ));

            // when
            List<Question> questions =
                    questionJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organization.getId());

            // then
            assertThat(questions)
                    .isSortedAccordingTo(Comparator.comparing(Question::getCreatedAt).reversed());
        }
    }
}
