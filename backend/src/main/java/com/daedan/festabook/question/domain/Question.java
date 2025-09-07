package com.daedan.festabook.question.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.domain.BaseEntity;
import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE question SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity implements Comparable<Question> {

    private static final int MAX_QUESTION_LENGTH = 255;
    private static final int MAX_ANSWER_LENGTH = 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, length = 1024)
    private String answer;

    @Column(nullable = false)
    private Integer sequence;

    public Question(
            Festival festival,
            String question,
            String answer,
            Integer sequence
    ) {
        validateFestival(festival);
        validateQuestion(question);
        validateAnswer(answer);
        validateSequence(sequence);

        this.festival = festival;
        this.question = question;
        this.answer = answer;
        this.sequence = sequence;
    }

    public void updateQuestionAndAnswer(String question, String answer) {
        validateQuestion(question);
        validateAnswer(answer);

        this.question = question;
        this.answer = answer;
    }

    public void updateSequence(Integer sequence) {
        validateSequence(sequence);

        this.sequence = sequence;
    }

    public boolean isFestivalIdEqualTo(Long festivalId) {
        return this.getFestival().getId().equals(festivalId);
    }

    @Override
    public int compareTo(Question otherQuestion) {
        return sequence.compareTo(otherQuestion.sequence);
    }

    private void validateFestival(Festival festival) {
        if (festival == null) {
            throw new BusinessException("Festival은 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new BusinessException("질문은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (question.length() > MAX_QUESTION_LENGTH) {
            throw new BusinessException(
                    String.format("질문은 %d자를 초과할 수 없습니다.", MAX_QUESTION_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new BusinessException("답변은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (answer.length() > MAX_ANSWER_LENGTH) {
            throw new BusinessException(
                    String.format("답변은 %d자를 초과할 수 없습니다.", MAX_ANSWER_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateSequence(Integer sequence) {
        if (sequence == null) {
            throw new BusinessException("순서는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (sequence < 0) {
            throw new BusinessException("순서는 음수일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
