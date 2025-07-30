package com.daedan.festabook.question.domain;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    private static final int MAX_QUESTION_LENGTH = 500;
    private static final int MAX_ANSWER_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private Integer sequence;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Question(
            Organization organization,
            String question,
            String answer,
            Integer sequence,
            LocalDateTime createdAt
    ) {
        validateOrganization(organization);
        validateQuestion(question);
        validateAnswer(answer);

        this.organization = organization;
        this.question = question;
        this.answer = answer;
        this.sequence = sequence;
        this.createdAt = createdAt;
    }

    public Question(
            Organization organization,
            String question,
            String answer,
            Integer sequence
    ) {
        this(
                organization,
                question,
                answer,
                sequence,
                null
        );
    }

    public void updateQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public void updateSequence(Integer sequence) {
        this.sequence = sequence;
    }

    private void validateOrganization(Organization organization) {
        if (organization == null) {
            throw new BusinessException("Organization은 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
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
}
