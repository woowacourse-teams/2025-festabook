package com.daedan.festabook.question.infrastructure;

import com.daedan.festabook.question.domain.Question;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {

    List<Question> findByFestivalIdOrderBySequenceAsc(Long festivalId);

    Integer countByFestivalId(Long festivalId);

    @Query("SELECT MAX(q.sequence) FROM Question q WHERE q.festival.id = :festivalId AND q.deleted = false")
    Optional<Integer> findMaxSequenceByFestivalId(@Param("festivalId") Long festivalId);
}
