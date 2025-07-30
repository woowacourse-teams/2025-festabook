package com.daedan.festabook.question.infrastructure;

import com.daedan.festabook.question.domain.Question;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {

    List<Question> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);

    Optional<Question> findTopByOrganizationIdOrderBySequenceDesc(Long organizationId);
}
