package com.daedan.festabook.announcement.infrastructure;

import com.daedan.festabook.announcement.domain.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswerJpaRepository extends JpaRepository<QuestionAnswer, Long> {
}
