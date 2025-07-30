package com.daedan.festabook.question.infrastructure;

import com.daedan.festabook.question.domain.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {

    List<Question> findByOrganizationIdOrderBySequenceDesc(Long organizationId);

    Integer countByOrganizationId(Long organizationId);
}
