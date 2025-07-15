package com.daedan.festabook.question.infrastructure;

import com.daedan.festabook.question.domain.QuestionAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswerJpaRepository extends JpaRepository<QuestionAnswer, Long> {

    List<QuestionAnswer> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
}
